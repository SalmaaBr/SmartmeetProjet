import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditFeedbackFrontComponent } from './edit-feedback-front.component';

describe('EditFeedbackFrontComponent', () => {
  let component: EditFeedbackFrontComponent;
  let fixture: ComponentFixture<EditFeedbackFrontComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [EditFeedbackFrontComponent]
    });
    fixture = TestBed.createComponent(EditFeedbackFrontComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
