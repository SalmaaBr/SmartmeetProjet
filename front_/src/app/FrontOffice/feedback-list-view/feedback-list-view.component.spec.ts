import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FeedbackListViewComponent } from './feedback-list-view.component';

describe('FeedbackListViewComponent', () => {
  let component: FeedbackListViewComponent;
  let fixture: ComponentFixture<FeedbackListViewComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [FeedbackListViewComponent]
    });
    fixture = TestBed.createComponent(FeedbackListViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
