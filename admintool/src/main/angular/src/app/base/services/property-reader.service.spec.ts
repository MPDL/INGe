import { TestBed, inject } from '@angular/core/testing';

import { PropertyReaderService } from './property-reader.service';

describe('PropertyReaderService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [PropertyReaderService]
    });
  });

  it('should be created', inject([PropertyReaderService], (service: PropertyReaderService) => {
    expect(service).toBeTruthy();
  }));
});
