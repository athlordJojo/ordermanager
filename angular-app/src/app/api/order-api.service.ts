import {Injectable, OnDestroy} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {OrderDto, OrderState} from '../model/order-dto';
import {Observable} from 'rxjs';
import * as SockJS from "sockjs-client";
import {CompatClient, Stomp, StompSubscription} from "@stomp/stompjs";

@Injectable()
export class OrderApi implements OnDestroy {

  private readonly url: string;
  private readonly client: CompatClient;

  constructor(private http: HttpClient) {
    this.url = '/companies/B28C343D-03C1-4FF1-90B9-5DDA8AFD3BFE';
    // this.url = 'http://localhost:8080/companies/B28C343D-03C1-4FF1-90B9-5DDA8AFD3BFE';


    this.client = Stomp.over(function () {
      return new SockJS('/liveupdates')
      // return new SockJS('http://localhost:8080/liveupdates')
    });

    this.client.reconnect_delay = 5000;
  }

  ngOnDestroy(): void {
    if (this.client != null && this.client.connected) {
      console.debug("Disconnecting stomp client")
      this.client.disconnect();
    }
  }

  public updates = new Observable((observer) => {
    let callback = message => {
      console.debug("received order update")
      let updateBody = JSON.parse(message.body);
      observer.next(updateBody)
    };
    let stompSubscription: StompSubscription;
    if (this.client.connected) {
      stompSubscription = this.client.subscribe("/topic/orders", callback)
    } else {
      this.client.connect({}, () => {
        // We are connected
        console.log("connected via websocket");
        this.client.subscribe("/topic/orders", callback)
      });
    }
    return {
      unsubscribe() {
        if (stompSubscription) {
          stompSubscription.unsubscribe();
        }
        observer.unsubscribe();
      }
    };
  });

  public findAll(): Observable<OrderDto[]> {
    return this.http.get<OrderDto[]>(this.url + "/orders");
  }

  public save(scoreBoardNumber: number, state:OrderState = OrderState.IN_PROGRESS): Observable<OrderDto> {
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
