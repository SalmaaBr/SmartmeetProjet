import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GeteventComponent } from './getevent.component';

describe('GeteventComponent', () => {
  let component: GeteventComponent;
  let fixture: ComponentFixture<GeteventComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [GeteventComponent]
    });
    fixture = TestBed.createComponent(GeteventComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
