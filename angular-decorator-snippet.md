Angular Log Decorator annotation snippet

```
logger.decorator.ts

/*
Extending ClassDecorator, the return function --> return a decorator factory
and extends feature by using prototype extension pattern.
*/
export function ConsoleLogger(): ClassDecorator  {
    return (target: Function) => {
        const ngOnInit: Function = target.prototype.ngOnInit;
        target.prototype.ngOnInit = function( ...args ) {

	           console.log('ngOnInit:', target.name);

            if ( ngOnInit ) {
                ngOnInit.apply(this, args);
            }
        };
    };
}

```
Usage:
```
TransactionComponent.ts

import { Component, Input, OnInit } from '@angular/core';
import { Logger } from './logger.decorator';
import { DxService } from './dx.service';

@Component({
  selector: 'tx-screen',
  templateUrl: './txscreen.component.html',
  styleUrls: ['./txscreen.component.css']
  
})


@Logger
export class TransactionComponent implements OnInit  {

  constructor(private dxService: DxService){}

  ngOnInit(){
    //do Stuff
    //call dxService
  }

  ///redacted methods

}
```
