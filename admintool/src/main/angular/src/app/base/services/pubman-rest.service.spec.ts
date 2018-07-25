import { TestBed, inject } from '@angular/core/testing';

import { PubmanRestService } from './pubman-rest.service';

describe('PubmanRestService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [PubmanRestService]
    });
  });

  it('should be created', inject([PubmanRestService], (service: PubmanRestService) => {
    expect(service).toBeTruthy();
  }));
});
