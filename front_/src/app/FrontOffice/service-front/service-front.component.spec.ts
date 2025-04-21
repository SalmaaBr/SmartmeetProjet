import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ServiceFrontComponent } from './service-front.component';

describe('ServiceFrontComponent', () => {
  let component: ServiceFrontComponent;
  let fixture: ComponentFixture<ServiceFrontComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ServiceFrontComponent]
    });
    fixture = TestBed.createComponent(ServiceFrontComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
