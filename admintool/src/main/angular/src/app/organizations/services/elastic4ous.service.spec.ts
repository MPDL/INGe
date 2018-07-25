import { TestBed, inject } from '@angular/core/testing';

import { Elastic4ousService } from './elastic4ous.service';

describe('Elastic4ousService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [Elastic4ousService]
    });
  });

  it('should be created', inject([Elastic4ousService], (service: Elastic4ousService) => {
    expect(service).toBeTruthy();
  }));
});
