import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MentalHealthComponent } from './mental-health.component';

describe('MentalHealthComponent', () => {
  let component: MentalHealthComponent;
  let fixture: ComponentFixture<MentalHealthComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MentalHealthComponent]
    });
    fixture = TestBed.createComponent(MentalHealthComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
