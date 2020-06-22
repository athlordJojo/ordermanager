import {Component, OnInit} from '@angular/core';
import {OrderApi} from '../api/order-api.service';
import {OrderDto, OrderState} from "../model/order-dto";
import {FormControl, FormGroup, ValidationErrors, ValidatorFn, Validators} from '@angular/forms';
import {OrderService} from "../service/order.service";

@Component({
  selector: 'app-order-form',
  templateUrl: './order-form.component.html',
  styleUrls: ['./order-form.component.css']
})
export class OrderFormComponent implements OnInit {
  newScoreBoardNumberForm: FormControl;

  orderState = OrderState
  ngOnInit(): void {
    this.loadData();
  }

  newOrderForm: FormGroup;
  selectedOrder: OrderDto;
  orders: OrderDto[] = [];
  allInProgressOrders: OrderDto[] = [];
  allReadyOrders: OrderDto[] = [];

  constructor(
    private orderApi: OrderApi,
    private orderService: OrderService) {
    this.newScoreBoardNumberForm = new FormControl('', [
      Validators.required,
      Validators.min(0),
      this.uniqueScoreBoardValidator
    ]);
    this.newOrderForm = new FormGroup({
      scoreboardnumber: this.newScoreBoardNumberForm
    });
  }

  createNewOrder() {
    this.orderApi.save(this.newScoreBoardNumberForm.value).subscribe(result => {
      this.newScoreBoardNumberForm.reset()
      this.loadData();
    });
  }

  uniqueScoreBoardValidator: ValidatorFn = (control: FormGroup): ValidationErrors | null => {
    const enteredValue = control.value;
    if (enteredValue === '' || !enteredValue) {
      return null;
    }
    let isScoreBoardNumberUsed = this.orderService.existOrderWithScoreboardNumber(this.orders, control.value);

    return isScoreBoardNumberUsed ? {'scoreBoardNumberIsUsed': true} : null;
  };


  loadData() {
    this.orderApi.findAll().subscribe(data => {
      this.orders = data;
      this.allInProgressOrders = this.orderService.filterAndSortByModifiedDate(data, OrderState.IN_PROGRESS)
      this.allReadyOrders = this.orderService.filterAndSortByModifiedDate(data, OrderState.READY)
      this.preselectOrder();
    })
  }

  private preselectOrder() {
    if (this.allInProgressOrders.length > 0) {
      this.selectedOrder = this.allInProgressOrders[0];
    } else if (this.allReadyOrders.length > 0) {
      this.selectedOrder = this.allReadyOrders[0];
    }
  }

  toggleOrder(selectedOrder: OrderDto) {
    // this toggles the selected item
    if (this.selectedOrder && this.selectedOrder.uuid == selectedOrder.uuid) {
      this.selectedOrder = null;
    } else {
      this.selectedOrder = selectedOrder;
    }
  }

  changeStateOfSelectedOrder(state: OrderState) {
    this.selectedOrder.state = state;
    this.orderApi.update(this.selectedOrder).subscribe(data => this.loadData());
    this.toggleOrder(this.selectedOrder);
  }

  deleteSelectedOrder(order: OrderDto) {
    this.orderApi.delete(order).subscribe(
      data => {
        this.loadData();
      });
  }

  getClassForListItem(order: OrderDto) {
    let classForRow: string

    if (order === this.selectedOrder) {
      classForRow = "list-group-item-primary active";
    } else {
      switch (order.state) {
        case OrderState.IN_PROGRESS: {
          classForRow = "list-group-item-secondary"
          break;
        }
        case OrderState.READY: {
          classForRow = "list-group-item-success"
          break;
        }
      }
    }
    return classForRow;
  }

  resetForm() {
    this.newScoreBoardNumberForm.reset();
  }

  addDigitToScoreBoardNumber(number: number) {
    let asString: String = this.newScoreBoardNumberForm.value !== null ? this.newScoreBoardNumberForm.value.toString() : "";
    this.newScoreBoardNumberForm.setValue(asString.concat(number.toString()));
  }

  deleteLastChar() {
    let length = this.newScoreBoardNumberForm.value.length;
    if (this.newScoreBoardNumberForm.value && length > 0) {
      let oneLess: String = this.newScoreBoardNumberForm.value.substring(0, length - 1);
      this.newScoreBoardNumberForm.setValue(oneLess);
    }
  }

  selectNextFreeNumber() {
    let nextScoreBoardNumber: String;

    if (this.orders.length > 0) {
      let sorted: OrderDto[] = this.orderService.sortByScoreBoardNumber(this.orders);

      let all: number[] = [];// a set containing all numbers from 0 to the biggest scoreboardnumber.

      // create set containing all numbers from 0 to the biggest available scoreboardnumber + 1.
      // the +1 is necessary because we want to make sure that we even get a difference when all numbers are there
      // from 0 to the biggest scoreboardnumber are assigned
      for (let i = 0; i <= sorted[sorted.length - 1].scoreBoardNumber + 1; i++) {
        all.push(i);
      }

      let allScoreBoardNumbers = sorted.map(value => value.scoreBoardNumber);
      // create a difference between all numbers and the already used numbers (set theory).
      // Now the 'difference'-array contains all numbers which are not yet assigned.
      let difference:number[] = all.filter(x => !allScoreBoardNumbers.includes(x));

      nextScoreBoardNumber = difference[0].toString();
    } else {
      // in case we dont have any scoreboardnumber yet
      nextScoreBoardNumber = "0";
    }

    this.newScoreBoardNumberForm.setValue(nextScoreBoardNumber);
  }
}
