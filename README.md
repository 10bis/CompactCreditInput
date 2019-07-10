
# CompactCreditInput  
A compact credit card input field that combines the number date and cvv into a single field  
  
[![Build Status](https://travis-ci.org/10bis/CompactCreditInput.svg?branch=master)](https://travis-ci.org/10bis/CompactCreditInput)  
[![Release](https://jitpack.io/v/10bis/compactCreditInput.svg)](https://jitpack.io/#10bis/compactCreditInput)  
  
  
<img src="gifs/demo.gif" alt="gif_demo" width="300"/> <img src="screenshots/screen_amex.png" alt="static_amex" width="300"/> <img src="screenshots/screen_visa.png" alt="static_visa" width="300"/>  
  
## Features  
  
 - Automatic card number formatting
 - Realtime date validation
 - Card type logo based on typed number
 - Lifecycle automatic disposal support
 - Keyboard events handling
 - Auto-fill compatible 
 - Copy & Paste support
 
## Installation
  
Add JitPack to your repositories  
  
    repositories { 
	    maven { url "https://jitpack.io" } 
    }  
And then add the dependency to your dependencies   
  
    dependencies { 
	    implementation "com.github.10bis:compactCreditInput:$latest_version" 
    }  
	 
## Usage  
Add the view to your layout file 
  
	<com.tenbis.support.views.CompactCreditInput  
	    android:id="@+id/compact_credit_card_input"  
	    android:layout_width="match_parent"  
	    android:layout_height="wrap_content" />


You can further customize the field in your layout file by changing 
  
 - `card_background`
 - `card_number_background_color`
 - `card_date_background_color`
 - `card_cvv_background_color`
 - `label_text`
 - `label_text_color`
 - `label_text_font`
 - `text_font`
 - `text_color`
 - `hint_color`
 - `card_number_hint`
 - `card_date_hint`
 - `card_cvv_hint`
 
Or in your code 
   
  - `cardBackground`
  - `cardNumberBackgroundColor`
  - `cardDateBackgroundColor`
  - `cardCvvBackgroundColor`
  - `labelText`
  - `labelTextColor`
  - `labelTextFont`
  - `textFont`
  - `textColor`
  - `hintColor`
  - `cardNumberHint`
  - `cardDateHint`
  - `cardCvvHint`


### Events
The library notifies when a change occur using the `OnCreditCardStateChanged` interface

 - Once all fields have valid inputs
   
        onCreditCardValid(creditCard: CreditCard)

 - Once a valid card number is entered
   
        onCreditCardNumberValid(creditCardNumber: String)

 - Once a valid expiration date is entered
   
        onCreditCardExpirationDateValid(month: Int, year: Int)

 - Once a valid cvv is entered
   
        onCreditCardCvvValid(cvv: String)

 - Once the card type is found
   
        onCreditCardTypeFound(cardType: CardType)

 - Once one of the fields is invalid
   
        onInvalidCardTyped()

You can subscribe to events by calling

    compactCreditCardInput.addOnCreditCardStateChangedListener(OnCreditCardStateChanged)
    
You can unsubscribe from events by calling

    compactCreditCardInput.removeOnCreditCardStateChangedListener(OnCreditCardStateChanged)
    
### Cleaning up
The library can clean it self automatically when it has an attached `Lifecycle`
You can attach your lifecycle by calling

    compactCreditCardInput.attachLifecycle(Lifecycle)
    
If you don't want to attach the lifecycle just call 

    compactCreditCardInput.onDestroy()
    
When your'e done with the field
  
## Contributing  
  
Please read [CONTRIBUTING](CONTRIBUTING.md) for details on our code of conduct, and the process for submitting pull requests to us.  
  
## Versioning  
  
We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/10bis/CompactCreditInput/tags).   
  
## Authors  
  
* **Gil Goldzweig Goldbuam** - *Initial work* - [Gil Goldzweig Goldbaum](https://github.com/gilgoldzweig)  
  
See also the list of [contributors](https://github.com/10bis/CompactCreditInput/contributors) who participated in this project.  
  
## License  
  
This project is licensed under the GNU General Public License v3.0 - see the [LICENSE.md](LICENSE.md) file for details
