import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ResourceReservationStatisticsComponent } from './resource-reservation-statistics.component';

describe('ResourceReservationStatisticsComponent', () => {
  let component: ResourceReservationStatisticsComponent;
  let fixture: ComponentFixture<ResourceReservationStatisticsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ResourceReservationStatisticsComponent]
    });
    fixture = TestBed.createComponent(ResourceReservationStatisticsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
