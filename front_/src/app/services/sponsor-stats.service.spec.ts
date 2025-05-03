import { TestBed } from '@angular/core/testing';

import { SponsorStatsService } from './sponsor-stats.service';

describe('SponsorStatsService', () => {
  let service: SponsorStatsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SponsorStatsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
