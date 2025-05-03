import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MentalHealthHistoryComponent } from './mental-health-history.component';

describe('MentalHealthHistoryComponent', () => {
  let component: MentalHealthHistoryComponent;
  let fixture: ComponentFixture<MentalHealthHistoryComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MentalHealthHistoryComponent]
    });
    fixture = TestBed.createComponent(MentalHealthHistoryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
