import { Directive, ElementRef, Input, OnChanges, SimpleChanges, Renderer2 } from '@angular/core';
import { ToastrService } from 'ngx-toastr';

@Directive({
  selector: '[appDisableDates]'
})
export class DisableDatesDirective implements OnChanges {
  @Input() appDisableDates: string[] = [];
  @Input() minDate: string = '';
  private datePickerStyleId = 'date-picker-style';

  constructor(
    private el: ElementRef,
    private renderer: Renderer2,
    private toastr: ToastrService
  ) {}

  ngOnChanges(changes: SimpleChanges): void {
    if ((changes['appDisableDates'] || changes['minDate']) && this.el.nativeElement) {
      this.setupDateInput();
      this.injectDatePickerStyles();
    }
  }

  private setupDateInput(): void {
    const input = this.el.nativeElement as HTMLInputElement;

    if (input.type !== 'date') {
      return;
    }

    // Set min date
    if (this.minDate) {
      input.min = this.minDate;
    }

    // Add event listener for when user interacts with date picker
    input.addEventListener('input', () => {
      const selectedDate = input.value;
      if (this.isDateDisabled(selectedDate)) {
        // Clear the input if a disabled date is selected
        setTimeout(() => {
          input.value = '';
          // Trigger change event to update form control value
          const event = new Event('change', { bubbles: true });
          input.dispatchEvent(event);

          // Show toast message
          this.toastr.error('This date is already reserved', 'Date unavailable');
        }, 0);
      }
    });

    // Add a click event listener to show calendar popup
    input.addEventListener('click', () => {
      // Force refresh styles whenever calendar opens
      this.injectDatePickerStyles();
    });
  }

  private injectDatePickerStyles(): void {
    // Remove existing style if any
    let existingStyle = document.getElementById(this.datePickerStyleId);
    if (existingStyle) {
      existingStyle.remove();
    }

    // Create style element
    const style = this.renderer.createElement('style');
    style.id = this.datePickerStyleId;
    style.type = 'text/css';

    // Generate CSS for disabling dates
    const disableDatesCSS = this.generateDisableDatesCSS();

    // Add style content
    const content = this.renderer.createText(disableDatesCSS);
    this.renderer.appendChild(style, content);

    // Add style to document head
    this.renderer.appendChild(document.head, style);
  }

  private generateDisableDatesCSS(): string {
    if (!this.appDisableDates || this.appDisableDates.length === 0) {
      return '';
    }

    // CSS to disable dates in the date picker
    let css = '';

    // Get current year and month
    const today = new Date();
    const currentYear = today.getFullYear();
    const currentMonth = today.getMonth();

    // Process each disabled date
    this.appDisableDates.forEach(dateStr => {
      const date = new Date(dateStr);
      const year = date.getFullYear();
      const month = date.getMonth();
      const day = date.getDate();

      // Generate CSS selector for this date
      // This works for Chrome, Edge, and other browsers using the calendar picker
      css += `
        input[type="date"]::-webkit-calendar-picker-indicator:hover {
          opacity: 1;
        }

        /* Style for disabled dates */
        input[type="date"]::-webkit-datetime-edit {
          position: relative;
          z-index: 1;
        }

        /* General style for date inputs with disabled dates */
        input[type="date"] {
          position: relative;
        }

        /* Create a tooltip for reserved dates */
        input[type="date"]::after {
          content: "Some dates are reserved";
          position: absolute;
          top: -30px;
          left: 0;
          background: #dc3545;
          color: white;
          padding: 3px 10px;
          border-radius: 4px;
          font-size: 12px;
          opacity: 0;
          transition: opacity 0.2s;
          pointer-events: none;
        }

        input[type="date"]:hover::after {
          opacity: 1;
        }
      `;
    });

    return css;
  }

  private isDateDisabled(dateStr: string): boolean {
    return this.appDisableDates.includes(dateStr);
  }
}
