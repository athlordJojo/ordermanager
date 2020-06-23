import {Injectable, OnDestroy} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {OrderDto, OrderState} from '../model/order-dto';
import {Observable} from 'rxjs';
import * as SockJS from "sockjs-client";
import {CompatClient, Stomp, StompSubscription} from "@stomp/stompjs";

@Injectable()
export class OrderApi implements OnDestroy {

  private readonly topicName: string = "/topic/orders";
  private readonly url: string;

  private readonly client: CompatClient;
  private readonly companyId: string = 'B28C343D-03C1-4FF1-90B9-5DDA8AFD3BFE';

  constructor(private http: HttpClient) {
    // this.url = `/companies/${this.companyId}/orders`
    this.url = 'http://localhost:8080/companies/B28C343D-03C1-4FF1-90B9-5DDA8AFD3BFE/orders';


    this.client = Stomp.over(function () {
      // return new SockJS('/liveupdates')
      return new SockJS('http://localhost:8080/liveupdates')
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
    let callback = (message: { body: string; }) => {
      console.debug("received order update")
      observer.next(JSON.parse(message.body))
    };
    let stompSubscription: StompSubscription;
    if (this.client.connected) {
      stompSubscription = this.client.subscribe(this.topicName, callback)
    } else {
      this.client.connect({}, () => {
        // We are connected
        console.log("connected via websocket");
        this.client.subscribe(this.topicName, callback)
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
    return this.http.get<OrderDto[]>(this.url);
  }

  public save(order: OrderDto): Observable<OrderDto> {
    return this.http.post<OrderDto>(this.url, order);
  }

  public update(order: OrderDto): Observable<OrderDto> {
    return this.http.put<OrderDto>(`${this.url}/${order.uuid}`, order);
  }

  public delete(order: OrderDto): Observable<OrderDto> {
    return this.http.delete<OrderDto>(`${this.url}/${order.uuid}`);
  }
}
