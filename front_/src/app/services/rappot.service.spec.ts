import { TestBed } from '@angular/core/testing';

import { RappotService } from './rappot.service';

describe('RappotService', () => {
  let service: RappotService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RappotService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
