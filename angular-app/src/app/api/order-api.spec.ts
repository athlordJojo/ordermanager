import {TestBed} from '@angular/core/testing';

import {OrderApi} from './order-api.service';
import {HttpClient} from "@angular/common/http";
import {of} from "rxjs";
import {OrderDto, OrderState} from "../model/order-dto";

describe('Order-api', () => {
  let service: OrderApi;
  let httpClientSpy
  let orderDto = new OrderDto();
  orderDto.title = "test order"
  const dummyResponse: OrderDto[] = [orderDto]

  beforeEach(() => {
    const httpSpy = jasmine.createSpyObj('HttpClient', ['get']);

    TestBed.configureTestingModule({
      // Provide both the service-to-test and its (spy) dependency
      providers: [
        OrderApi,
        {provide: HttpClient, useValue: httpSpy}
      ]
    });


    TestBed.configureTestingModule({});
    service = TestBed.inject(OrderApi);
    httpClientSpy = TestBed.inject(HttpClient)
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('find all return response expect success', () => {
    httpClientSpy.get.and.returnValue(of(dummyResponse));
    service.findAll().subscribe(orders => {
      expect(orders).toEqual(dummyResponse)
    }, fail)
  });
});
