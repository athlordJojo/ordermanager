import { TestBed } from '@angular/core/testing';

import { CssHelperService } from './css-helper.service';

describe('CssHelperService', () => {
  let service: CssHelperService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CssHelperService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
