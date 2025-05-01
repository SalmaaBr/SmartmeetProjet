import { TestBed } from '@angular/core/testing';

import { ContentModerationService } from './content-moderation.service';

describe('ContentModerationService', () => {
  let service: ContentModerationService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ContentModerationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
