import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {OrderDto, OrderState} from "../model/order-dto";
import {CssHelperService} from "../service/css-helper.service";

@Component({
  selector: 'app-manage-list',
  templateUrl: './manage-list.component.html',
  styleUrls: ['./manage-list.component.css']
})
export class ManageListComponent implements OnInit {
  @Input() orders: OrderDto[]
  @Input() selectedOrder: OrderDto
  @Input() description: string
  @Output() clickedItem = new EventEmitter<OrderDto>();
  cssHelperService: CssHelperService;

  constructor(cssHelperService: CssHelperService) {
    this.cssHelperService = cssHelperService;
  }

  ngOnInit(): void {
  }

  handleClick(order: OrderDto) {
    this.clickedItem.emit(order)
  }
}
