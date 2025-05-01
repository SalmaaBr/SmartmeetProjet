import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MentalHealthListComponent } from './mental-health-list.component';

describe('MentalHealthListComponent', () => {
  let component: MentalHealthListComponent;
  let fixture: ComponentFixture<MentalHealthListComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MentalHealthListComponent]
    });
    fixture = TestBed.createComponent(MentalHealthListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
