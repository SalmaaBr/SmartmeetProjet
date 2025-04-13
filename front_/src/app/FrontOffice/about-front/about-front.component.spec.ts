import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AboutFrontComponent } from './about-front.component';

describe('AboutFrontComponent', () => {
  let component: AboutFrontComponent;
  let fixture: ComponentFixture<AboutFrontComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AboutFrontComponent]
    });
    fixture = TestBed.createComponent(AboutFrontComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
