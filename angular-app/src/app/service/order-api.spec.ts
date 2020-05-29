import { TestBed } from '@angular/core/testing';

import { OrderApi } from './order-api.service';

describe('OrderServiceService', () => {
  let service: OrderApi;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OrderApi);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
