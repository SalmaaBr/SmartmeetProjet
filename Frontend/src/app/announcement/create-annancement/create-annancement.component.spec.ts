import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateAnnancementComponent } from './create-annancement.component';

describe('CreateAnnancementComponent', () => {
  let component: CreateAnnancementComponent;
  let fixture: ComponentFixture<CreateAnnancementComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CreateAnnancementComponent]
    });
    fixture = TestBed.createComponent(CreateAnnancementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
