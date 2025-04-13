import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateRecutementComponent } from './create-recutement.component';

describe('CreateRecutementComponent', () => {
  let component: CreateRecutementComponent;
  let fixture: ComponentFixture<CreateRecutementComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CreateRecutementComponent]
    });
    fixture = TestBed.createComponent(CreateRecutementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
