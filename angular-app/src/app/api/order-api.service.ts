import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {OrderDto} from '../model/order-dto';
import {Observable} from 'rxjs';
import * as SockJS from "sockjs-client";
import {CompatClient, Stomp} from "@stomp/stompjs";

@Injectable()
export class OrderApi {

  private readonly url: string;
  private client: CompatClient;

  constructor(private http: HttpClient) {
    // this.url = '/companies/B28C343D-03C1-4FF1-90B9-5DDA8AFD3BFE';
    // var host = "http://localhost"
    this.url = 'http://localhost/companies/B28C343D-03C1-4FF1-90B9-5DDA8AFD3BFE';

    this.client = Stomp.over(new SockJS('http://localhost:80/liveupdates'));
    this.client.reconnect_delay = 5000;
    // this.client.connect({}, () => {
    //   // We are connected
    //   console.log("connected");
    // });
  }

  public updates = new Observable((observer) => {
    this.client.connect({}, () => {
      // We are connected
      console.log("connected");
      this.client.subscribe("/topic/orders", message => {
        console.log("received order update")
        let updateBody = JSON.parse(message.body);
        console.log(updateBody)
        observer.next(updateBody)
      })
    });
    return {
      unsubscribe() {
        // navigator.geolocation.clearWatch(watchId);
      }
    };
  });

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
