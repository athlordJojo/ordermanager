import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {OrderDto} from '../model/order-dto';
import {Observable} from 'rxjs';

@Injectable()
export class OrderService {

  private url: string;

  constructor(private http: HttpClient) {
    this.url = 'http://localhost:8080/companies/B28C343D-03C1-4FF1-90B9-5DDA8AFD3BFE';
  }

  public findAll(): Observable<OrderDto[]> {
    return this.http.get<OrderDto[]>(this.url + "/orders");
  }

  public save(user: OrderDto) {
    return this.http.post<OrderDto>(this.url, user);
  }
}
