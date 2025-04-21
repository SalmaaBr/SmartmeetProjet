import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ResourceReservationManagementComponent } from './resource-reservation-management.component';

describe('ResourceReservationManagementComponent', () => {
  let component: ResourceReservationManagementComponent;
  let fixture: ComponentFixture<ResourceReservationManagementComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ResourceReservationManagementComponent]
    });
    fixture = TestBed.createComponent(ResourceReservationManagementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
