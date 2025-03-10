import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddInteractivePublicationComponent } from './add-interactive-publication.component';

describe('AddInteractivePublicationComponent', () => {
  let component: AddInteractivePublicationComponent;
  let fixture: ComponentFixture<AddInteractivePublicationComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AddInteractivePublicationComponent]
    });
    fixture = TestBed.createComponent(AddInteractivePublicationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
