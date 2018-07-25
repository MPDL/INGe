import { TestBed, inject } from '@angular/core/testing';

import { Elastic4usersService } from './elastic4users.service';

describe('Elastic4usersService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [Elastic4usersService]
    });
  });

  it('should be created', inject([Elastic4usersService], (service: Elastic4usersService) => {
    expect(service).toBeTruthy();
  }));
});
