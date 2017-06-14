<font size="20"><b>Struct</b> : <br>
	markup.language {}
	<br></font>
Last added: variables, pointers, linker, macros (preprocessor), multidem arrays and primitive operators.
	
	
<br><b><a href="https://github.com/henryco/Struct/tree/master/src/examples">-> Code examples</a>
<br><b><a href="https://github.com/henryco/Struct/blob/master/src/net/henryco/struct/parser/drivers/">-> Syntax analyzer</a>
<br><br>
<h2>Example</h2>

```
#sugar 'string\[\]' "java.lang.String[]"
#import struct
#sugar pckgUrl "com.game.render.fbo.psProcess.lights.stdLIght.userState"

var {
	typlght {
		type "com.game.render.fbo.psProcess.lights.type.EscapyLightType"
		val.java.global("com.game.render.fbo.psProcess.lights.type.EscapyLightSrcFactory", RND_64)
	}
	scl([float, 14]);
	xpos([float, 500]);
	ypos([float, 500]);
	
	alphaSwitch([string[], ["alphaSwitch", 0.9, 1]]);
	alphaSwitchRev([string[], ["alphaSwitch", 1, 0.9]]);
	holdSwitch([string[], [""]]);
}

EscapyGdx  {

	*lightType = var::typlght
	*scale = var::scl
	*x = var::xpos
	*y = var::ypos
	*alpha1 = var::alphaSwitch
	*alpha2 = var::alphaSwitchRev
	*hold = var::holdSwitch

	EscapyStdLight(&lightType, &scale, &x, &y);
	EscapyStdLight --> function {
		setColor([int, 0], [int, 0], [int, 255]);
		setVisible([boolean, true]);
		setPeriodicActions(&alpha1, &hold, &alpha2, &hold);
		setPeriods([float[], [1000, 3000, 1000, 2000, 250, 100, 200, 100]]); 
	}
	EscapyStdLight --> field {

	}
	EscapyStdLight.package: pckgUrl
}
```



