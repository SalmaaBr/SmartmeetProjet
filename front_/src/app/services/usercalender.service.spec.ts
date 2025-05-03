import { TestBed } from '@angular/core/testing';

import { UsercalenderService } from './usercalender.service';

describe('UsercalenderService', () => {
  let service: UsercalenderService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UsercalenderService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
