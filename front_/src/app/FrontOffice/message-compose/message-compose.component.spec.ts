import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MessageComposeComponent } from './message-compose.component';

describe('MessageComposeComponent', () => {
  let component: MessageComposeComponent;
  let fixture: ComponentFixture<MessageComposeComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MessageComposeComponent]
    });
    fixture = TestBed.createComponent(MessageComposeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
