import { TestBed } from '@angular/core/testing';

import { EventLikeService } from './event-like.service';

describe('EventLikeService', () => {
  let service: EventLikeService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(EventLikeService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
