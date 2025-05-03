import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MentalHealthHistoryBackComponent } from './mental-health-history-back.component';

describe('MentalHealthHistoryBackComponent', () => {
  let component: MentalHealthHistoryBackComponent;
  let fixture: ComponentFixture<MentalHealthHistoryBackComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MentalHealthHistoryBackComponent]
    });
    fixture = TestBed.createComponent(MentalHealthHistoryBackComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
