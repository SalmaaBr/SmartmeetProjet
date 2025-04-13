import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MonitoringRecruitmentListComponent } from './monitoring-recruitment-list.component';

describe('MonitoringRecruitmentListComponent', () => {
  let component: MonitoringRecruitmentListComponent;
  let fixture: ComponentFixture<MonitoringRecruitmentListComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MonitoringRecruitmentListComponent]
    });
    fixture = TestBed.createComponent(MonitoringRecruitmentListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
