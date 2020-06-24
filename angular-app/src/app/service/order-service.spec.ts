import {TestBed} from '@angular/core/testing';

import {OrderService} from './order.service';
import {OrderDto, OrderState} from "../model/order-dto";

describe('OrderServiceService', () => {
  let service: OrderService;
  let progressOrder1: OrderDto
  let progressOrder2: OrderDto
  let progressOrder3: OrderDto

  let readyOrder1: OrderDto
  let readyOrder2: OrderDto
  let readyOrder3: OrderDto

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OrderService);

    progressOrder1 = new OrderDto();
    progressOrder1.state = OrderState.IN_PROGRESS
    progressOrder1.scoreBoardNumber = 1
    progressOrder1.lastModifiedDate = new Date(2020, 0O5, 0O5, 17, 23, 42, 11);

    progressOrder2 = new OrderDto();
    progressOrder2.state = OrderState.IN_PROGRESS
    progressOrder2.scoreBoardNumber = 2
    progressOrder2.lastModifiedDate = new Date(2018, 0O5, 0O5, 17, 24, 42, 11);

    progressOrder3 = new OrderDto();
    progressOrder3.state = OrderState.IN_PROGRESS
    progressOrder3.scoreBoardNumber = 3
    progressOrder3.lastModifiedDate = new Date(2016, 0O5, 0O5, 16, 24, 42, 11);

    readyOrder1 = new OrderDto();
    readyOrder1.state = OrderState.READY
    readyOrder1.scoreBoardNumber = 4
    readyOrder1.lastModifiedDate = new Date(2019, 0O5, 0O5, 16, 24, 42, 11);

    readyOrder2 = new OrderDto();
    readyOrder2.state = OrderState.READY
    readyOrder1.scoreBoardNumber = 5
    readyOrder2.lastModifiedDate = new Date(2017, 0O5, 0O5, 16, 24, 42, 11);

    readyOrder3 = new OrderDto();
    readyOrder3.state = OrderState.READY
    readyOrder1.scoreBoardNumber = 5
    readyOrder3.lastModifiedDate = new Date(2015, 0O5, 0O5, 16, 24, 42, 11);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('orders asc sorted should return asc order', () => {
    let inputAscOrders: Array<OrderDto> = [progressOrder3, progressOrder2, progressOrder1];
    let expectedResult = [progressOrder3, progressOrder2, progressOrder1]
    let result: Array<OrderDto> = service.filterAndSortByModifiedDate(inputAscOrders, OrderState.IN_PROGRESS);
    expect(result).toEqual(expectedResult)
  });

  it('orders desc sorted should return asc order', () => {
    let inputDescOrders: Array<OrderDto> = [progressOrder1, progressOrder2, progressOrder3];
    let expectedResult = [progressOrder3, progressOrder2, progressOrder1]
    let result: Array<OrderDto> = service.filterAndSortByModifiedDate(inputDescOrders, OrderState.IN_PROGRESS);
    expect(result).toEqual(expectedResult)
  });

  it('filterAndSortByModifiedDate: orders not sorted should return asc order', () => {
    let input: Array<OrderDto> = [progressOrder2, progressOrder1, progressOrder3];
    let expectedResult = [progressOrder3, progressOrder2, progressOrder1]
    let result: Array<OrderDto> = service.filterAndSortByModifiedDate(input, OrderState.IN_PROGRESS);
    expect(result).toEqual(expectedResult)
  });

  it('filterAndSortByModifiedDate: orders not sorted and with both states should return asc order and only in_progress', () => {
    let input: Array<OrderDto> = [progressOrder2, readyOrder1, progressOrder1, readyOrder2, progressOrder3, readyOrder3];
    let expectedResult = [progressOrder3, progressOrder2, progressOrder1]
    let result: Array<OrderDto> = service.filterAndSortByModifiedDate(input, OrderState.IN_PROGRESS);
    expect(result).toEqual(expectedResult)
  });

  it('filterAndSortByModifiedDate: orders not sorted and with both states should return asc order and only ready', () => {
    let input: Array<OrderDto> = [progressOrder2, readyOrder1, progressOrder1, readyOrder2, progressOrder3, readyOrder3];
    let expectedResult = [readyOrder3, readyOrder2, readyOrder1]
    let result: Array<OrderDto> = service.filterAndSortByModifiedDate(input, OrderState.READY);
    expect(result).toEqual(expectedResult)
  });

  it('filterAndSortByModifiedDate: orders all in_progress filter for ready, should return empty array', () => {
    let inputAscOrders: Array<OrderDto> = [progressOrder3, progressOrder2, progressOrder1];
    let expectedResult = []
    let result: Array<OrderDto> = service.filterAndSortByModifiedDate(inputAscOrders, OrderState.READY);
    expect(result).toEqual(expectedResult)
  });

  it('filterAndSortByModifiedDate: empty array and should return an empty array', () => {
    let input: Array<OrderDto> = [];
    let expectedResult = []
    let result: Array<OrderDto> = service.filterAndSortByModifiedDate(input, OrderState.IN_PROGRESS);
    expect(result).toEqual(expectedResult)
  });

  it('sortByScoreBoardNumber: orders asc sorted, should return asc order', () => {
    let input: Array<OrderDto> = [progressOrder1, progressOrder2, progressOrder3];
    let expectedResult = [progressOrder1, progressOrder2, progressOrder3]
    let result: Array<OrderDto> = service.sortByScoreBoardNumber(input);
    expect(result).toEqual(expectedResult)
  });

  it('sortByScoreBoardNumber: orders desc sorted, should return asc order', () => {
    let input: Array<OrderDto> = [progressOrder3, progressOrder2, progressOrder1];
    let expectedResult = [progressOrder1, progressOrder2, progressOrder3]
    let result: Array<OrderDto> = service.sortByScoreBoardNumber(input);
    expect(result).toEqual(expectedResult)
  });

  it('sortByScoreBoardNumber: orders not sorted, should return asc order', () => {
    let input: Array<OrderDto> = [progressOrder2, progressOrder1, progressOrder3];
    let expectedResult = [progressOrder1, progressOrder2, progressOrder3]
    let result: Array<OrderDto> = service.sortByScoreBoardNumber(input);
    expect(result).toEqual(expectedResult)
  });

  it('sortByScoreBoardNumber: empty array should return empty array', () => {
    let input: Array<OrderDto> = [];
    let expectedResult = []
    let result: Array<OrderDto> = service.sortByScoreBoardNumber(input);
    expect(result).toEqual(expectedResult)
  });

  it('existOrderWithScoreboardNumber: array does contain matching order, should return true', () => {
    let input: Array<OrderDto> = [progressOrder3, progressOrder1, progressOrder2];
    let result: boolean = service.existOrderWithScoreboardNumber(input, 1);
    expect(result).toBeTrue()
  });

  it('existOrderWithScoreboardNumber: array does not contain matching order, should return true', () => {
    let input: Array<OrderDto> = [progressOrder3, progressOrder2];
    let result: boolean = service.existOrderWithScoreboardNumber(input, 1);
    expect(result).toBeFalsy()
  });

  it('sliceIntoSubArrays: empty array should return empty array', () => {
    let input: Array<OrderDto> = [];
    let expectedResult = []
    let result: Array<Array<OrderDto>> = service.sliceIntoSubArrays(input, 1);
    expect(result).toEqual(expectedResult)
  });

  it('sliceIntoSubArrays with chunksize=2: 6 orders should should be splitted into 3 arrays of 2 elements', () => {
    let input: Array<OrderDto> = [progressOrder1, progressOrder2, progressOrder3, readyOrder1, readyOrder2, readyOrder3];
    let expectedResult = [[progressOrder1, progressOrder2], [progressOrder3, readyOrder1], [readyOrder2, readyOrder3]]
    let result: Array<Array<OrderDto>> = service.sliceIntoSubArrays(input, 2);
    expect(result).toEqual(expectedResult)
  });

  it('sliceIntoSubArrays with chunksize=4: 6 orders should should be splitted into 2 arrays of 4 and 2 elements', () => {
    let input: Array<OrderDto> = [progressOrder1, progressOrder2, progressOrder3, readyOrder1, readyOrder2, readyOrder3];
    let expectedResult = [[progressOrder1, progressOrder2, progressOrder3, readyOrder1], [readyOrder2, readyOrder3]]
    let result: Array<Array<OrderDto>> = service.sliceIntoSubArrays(input, 4);
    expect(result).toEqual(expectedResult)
  });

  it('sliceIntoSubArrays with chunksize=7: 6 orders should should be splitted into 1 arrays of 7 elements', () => {
    let input: Array<OrderDto> = [progressOrder1, progressOrder2, progressOrder3, readyOrder1, readyOrder2, readyOrder3];
    let expectedResult = [[progressOrder1, progressOrder2, progressOrder3, readyOrder1, readyOrder2, readyOrder3]]
    let result: Array<Array<OrderDto>> = service.sliceIntoSubArrays(input, 7);
    expect(result).toEqual(expectedResult)
  });
});
