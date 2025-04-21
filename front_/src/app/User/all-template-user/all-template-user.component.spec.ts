import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AllTemplayeUserComponent } from './all-template-user.component';

describe('AllTemplayeUserComponent', () => {
  let component: AllTemplayeUserComponent;
  let fixture: ComponentFixture<AllTemplayeUserComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AllTemplayeUserComponent]
    });
    fixture = TestBed.createComponent(AllTemplayeUserComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
