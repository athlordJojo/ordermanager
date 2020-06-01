import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {OrderDto} from '../model/order-dto';
import {Observable} from 'rxjs';
import * as SockJS from "sockjs-client";
import {Stomp} from "@stomp/stompjs";

@Injectable()
export class OrderApi {

  private readonly url: string;

  constructor(private http: HttpClient) {
    // this.url = '/companies/B28C343D-03C1-4FF1-90B9-5DDA8AFD3BFE';
    this.url = 'http://localhost/companies/B28C343D-03C1-4FF1-90B9-5DDA8AFD3BFE';

    const client = Stomp.over(new SockJS('http://localhost:80/ordermanager'));
    client.reconnect_delay = 5000;
    client.connect({}, () => {
      // We are connected
      console.log("connected");
      client.subscribe("/topic/orders", message => {
        console.log("received update")
        console.log(JSON.parse(message.body))
      })
    });
  }

  public findAll(): Observable<OrderDto[]> {
    return this.http.get<OrderDto[]>(this.url + "/orders");
  }

  public save(scoreBoardNumber: number, state = "IN_PROGRESS"): Observable<OrderDto> {
    let newOrder: OrderDto = new OrderDto();
    newOrder.state = state;
    newOrder.scoreBoardNumber = scoreBoardNumber;
    return this.http.post<OrderDto>(this.url + "/orders", newOrder);
  }

  public update(order: OrderDto): Observable<OrderDto> {
    return this.http.put<OrderDto>(this.url + "/orders/" + order.uuid, order);
  }

  public delete(order: OrderDto): Observable<OrderDto> {
    return this.http.delete<OrderDto>(this.url + "/orders/" + order.uuid);
  }
}
