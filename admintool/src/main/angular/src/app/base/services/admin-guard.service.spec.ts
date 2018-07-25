import { TestBed, inject } from '@angular/core/testing';

import { AdminGuard } from './admin-guard.service';

describe('AdminGuard', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [AdminGuard]
    });
  });

  it('should be created', inject([AdminGuard], (service: AdminGuard) => {
    expect(service).toBeTruthy();
  }));
});
