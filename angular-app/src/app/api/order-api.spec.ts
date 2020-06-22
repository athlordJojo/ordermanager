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
  orderDto.uuid = "12345"
  const dummyResponse: OrderDto[] = [orderDto]

  beforeEach(() => {
    const httpSpy = jasmine.createSpyObj('HttpClient', ['get', 'delete', 'put']);

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
    expect(httpClientSpy.get).toHaveBeenCalledWith("/companies/B28C343D-03C1-4FF1-90B9-5DDA8AFD3BFE/orders")
  });

  it('updates the given order expect success', () => {
    httpClientSpy.put.and.returnValue(of("needs a parameter, otherwise oberserveable is not executed"));
    service.update(orderDto).subscribe(orders => {
      // nothing to check here
    }, fail)
    expect(httpClientSpy.put).toHaveBeenCalledWith("/companies/B28C343D-03C1-4FF1-90B9-5DDA8AFD3BFE/orders/12345", orderDto)
  });

  it('deletes the given order expect success', () => {
    httpClientSpy.delete.and.returnValue(of("needs a parameter, otherwise oberserveable is not executed"));
    service.delete(orderDto).subscribe(orders => {
      // nothing to check here
    }, fail)
    expect(httpClientSpy.delete).toHaveBeenCalledWith("/companies/B28C343D-03C1-4FF1-90B9-5DDA8AFD3BFE/orders/12345")
  });
});
