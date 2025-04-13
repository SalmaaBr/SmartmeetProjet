import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditRecruitmentComponent } from './edit-recruitment.component';

describe('EditRecruitmentComponent', () => {
  let component: EditRecruitmentComponent;
  let fixture: ComponentFixture<EditRecruitmentComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [EditRecruitmentComponent]
    });
    fixture = TestBed.createComponent(EditRecruitmentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
