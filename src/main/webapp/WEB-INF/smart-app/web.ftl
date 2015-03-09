<html>
<body>
form: ${ui.form!}<br>
<form>
<input type="hidden" name="action" value="selectSmartSession">
<select name="smartSession" onchange="submit()">
[#list this.activeSessions as activeSession]
<option value="${activeSession}" [#if this.activeSessionKey == activeSession]selected[/#if]>${this.toSessionDate(activeSession).time?time}</option>
[/#list]
</select>
</form>
<a href="?action=newSmartSession">new session</a>
<a href="?action=removeSmartSession">remove session</a>
</body>
</html>
