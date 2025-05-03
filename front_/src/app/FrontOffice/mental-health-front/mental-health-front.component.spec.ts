import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MentalHealthFrontComponent } from './mental-health-front.component';

describe('MentalHealthFrontComponent', () => {
  let component: MentalHealthFrontComponent;
  let fixture: ComponentFixture<MentalHealthFrontComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MentalHealthFrontComponent]
    });
    fixture = TestBed.createComponent(MentalHealthFrontComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
