import { TestBed } from '@angular/core/testing';

import { RecutementService } from './recutement.service';

describe('RecutementService', () => {
  let service: RecutementService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RecutementService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
