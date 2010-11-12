

function changeRuleElement(element)
{
	if (typeof element.selectedIndex != 'undefined' && element.options[element.selectedIndex].value == 'INPUT')
	{
		alert('creating input from' + element.name);
		var input = document.createElement('input');
		input.setAttribute('type', 'text');
		input.setAttribute('value', 'value');
		input.setAttribute('name', 'value');
		input.setAttribute('onchange', 'changeRuleElement(this)');
		element.parentNode.replaceChild(input, element);
	}
	
	var ruleElement = document.form['rule-element'].options[document.form['rule-element'].selectedIndex].value;
	var ruleText = ruleElement + ': rule1:';
	
	for (var i = 0; i < document.form.elements.length; i++)
	{
		var el = document.form.elements[i];
		if (el.name == 'rule-element')
		{
			
		}
		else if (el.name == 'message')
		{
			ruleText += ' else report ' + el.value;
		}
		else if (typeof el.selectedIndex != 'undefined')
		{
			if (el.options[el.selectedIndex].value == '')
			{
				break;
			}
			else
			{
				ruleText += ' ' + el.options[el.selectedIndex].value;
			}
		}
		else if (el.value == '')
		{
			alert('break');
			break;
		}
		else if (el.type = 'text' && el.name != '')
		{
			ruleText += ' \'' + el.value + '\'';
		}

	}
	
	location.href = '?rule-text=' + escape(ruleText);
}

function addSubsection(element)
{
	var html = '<select name="andor" size="1" onchange="changeRuleElement(this)"><option value="or">or</option><option value="and">and</option></select>';
	
	var select = document.createElement('select');
	select.setAttribute('size', '1');
	select.setAttribute('name', 'andor');
	select.setAttribute('onchange', 'changeRuleElement(this)');
	select.innerHTML = '<option value="or">or</option><option value="and">and</option>';
	element.parentNode.replaceChild(select, element);
	//changeRuleElement(element);
}