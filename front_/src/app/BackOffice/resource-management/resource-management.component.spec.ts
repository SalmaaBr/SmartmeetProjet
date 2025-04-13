import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ResourceManagementComponent } from './resource-management.component';

describe('ResourceManagementComponent', () => {
  let component: ResourceManagementComponent;
  let fixture: ComponentFixture<ResourceManagementComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ResourceManagementComponent]
    });
    fixture = TestBed.createComponent(ResourceManagementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
