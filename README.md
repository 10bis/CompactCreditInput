
# CompactCreditInput  
A compact credit card input field that combines the number date and cvv into a single field  
  
[![Build Status](https://travis-ci.org/10bis/CompactCreditInput.svg?branch=master)](https://travis-ci.org/10bis/CompactCreditInput)  
[![Release](https://jitpack.io/v/10bis/compactCreditInput.svg)](https://jitpack.io/#10bis/compactCreditInput)  
  
  
<img src="screenshots/screen_amex.png" alt="static_amex" width="300"/> <img src="screenshots/screen_visa.png" alt="static_visa" width="300"/>  
  
## Features  
  
 - Automatic card number formatting
 - Realtime date validation
 - Card type logo based on typed number
 - LifecycleOwner support
 - Keyboard events handling
 - Auto fill compatible 
 - Copy & Paste support  
 
## Getting Started  
  
Add JitPack to your repositories  
  
	 repositories { 
		 maven { url "https://jitpack.io" } 
	 }  
And then add the dependency to your dependencies   
  
	 dependencies { 
		 implementation "com.github.10bis:compactCreditInput:$latest_version" 
	 }  
### Simple usage  
Add the view to your layout file 
  
	<com.tenbis.support.views.CompactCreditInput  
	    android:id="@+id/main_activity_credit_card"  
	    android:layout_width="match_parent"  
	    android:layout_height="wrap_content" />

In Your Fragment/Activity

implement the card state change

	class MainActivity : AppCompatActivity(), OnCreditCardStateChanged {

implement the listener functions 

	override fun onCreditCardCompleted(creditCard: CreditCard) {  
		//Credit card is available 
	}  
	  
	override fun onInvalidCardTyped() {  
	 //Credit card is unavailable 
	}
	
In your `onCreate` attach the lifecycle and add the state listener
	
	main_activity_credit_card.attachLifecycle(lifecycle)  
	main_activity_credit_card.addOnCreditCardStateChangedListener(this)

That's it 
  
## Contributing  
  
Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our code of conduct, and the process for submitting pull requests to us.  
  
## Versioning  
  
We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/10bis/CompactCreditInput/tags).   
  
## Authors  
  
* **Gil Goldzweig Goldbuam** - *Initial work* - [Gil Goldzweig Goldbaum](https://github.com/gilgoldzweig)  
  
See also the list of [contributors](https://github.com/10bis/CompactCreditInput/contributors) who participated in this project.  
  
## License  
  
This project is licensed under the GNU General Public License v3.0 - see the [LICENSE.md](LICENSE.md) file for details