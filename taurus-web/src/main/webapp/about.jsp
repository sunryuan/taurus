<%@ page contentType="text/html;charset=utf-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<%@ include file="jsp/common-header.jsp"%>
<link href="css/docs.css" rel="stylesheet">
</head>
<body data-spy="scroll" data-target=".bs-docs-sidebar">
	<%@ include file="jsp/common-nav.jsp"%>
	<%@ include file="jsp/common-api.jsp"%>

	<div class="container">
		<div class="row">
			<div class="span3 bs-docs-sidebar">
				<ul class="nav nav-list bs-docs-sidenav affix">
					<li class="active"><a href="#crontab"><i
							class="icon-chevron-right"></i> Crontab</a></li>
					<li><a href="#status"><i class="icon-chevron-right"></i>
							Status</a></li>
					<li><a href="#autokill"><i class="icon-chevron-right"></i>
							AutoKill</a></li>
				</ul>
			</div>

			<div class="span9">
				<section id="crontab">
					<div class="page-header">
						<h1>Crontab表达式</h1>
					</div>
					<h3>Taurus会在填写的5位的cron表达式前自动加0，成为能够被解析的6位表达式。以下是表达式的规范文档。</h3>
					<P>Cron expressions are comprised of 6 required fields and one
						optional field separated by white space. The fields respectively
						are described as follows:
					<table cellspacing="8">
						<tr>
							<th align="left">Field Name</th>
							<th align="left">&nbsp;</th>
							<th align="left">Allowed Values</th>
							<th align="left">&nbsp;</th>
							<th align="left">Allowed Special Characters</th>
						</tr>
						<tr>
							<td align="left"><code>Seconds</code></td>
							<td align="left">&nbsp;
							</th>
							<td align="left"><code>0-59</code></td>
							<td align="left">&nbsp;
							</th>
							<td align="left"><code>, - /</code></td>
						</tr>
						<tr>
							<td align="left"><code>Minutes</code></td>
							<td align="left">&nbsp;
							</th>
							<td align="left"><code>0-59</code></td>
							<td align="left">&nbsp;
							</th>
							<td align="left"><code>, - /</code></td>
						</tr>
						<tr>
							<td align="left"><code>Hours</code></td>
							<td align="left">&nbsp;
							</th>
							<td align="left"><code>0-23</code></td>
							<td align="left">&nbsp;
							</th>
							<td align="left"><code>, - /</code></td>
						</tr>
						<tr>
							<td align="left"><code>Day-of-month</code></td>
							<td align="left">&nbsp;
							</th>
							<td align="left"><code>1-31</code></td>
							<td align="left">&nbsp;
							</th>
							<td align="left"><code>, - ? / L W</code></td>
						</tr>
						<tr>
							<td align="left"><code>Month</code></td>
							<td align="left">&nbsp;
							</th>
							<td align="left"><code>1-12 or JAN-DEC</code></td>
							<td align="left">&nbsp;
							</th>
							<td align="left"><code>, - /</code></td>
						</tr>
						<tr>
							<td align="left"><code>Day-of-Week</code></td>
							<td align="left">&nbsp;
							</th>
							<td align="left"><code>1-7 or SUN-SAT</code></td>
							<td align="left">&nbsp;
							</th>
							<td align="left"><code>, - ? / L #</code></td>
						</tr>
						<tr>
							<td align="left"><code>Year (Optional)</code></td>
							<td align="left">&nbsp;
							</th>
							<td align="left"><code>empty, 1970-2199</code></td>
							<td align="left">&nbsp;
							</th>
							<td align="left"><code>, - /</code></td>
						</tr>
					</table>
					<P>The '' character is used to specify all values. For example,
						&quot;&quot; in the minute field means &quot;every minute&quot;.
					<P>The '?' character is allowed for the day-of-month and
						day-of-week fields. It is used to specify 'no specific value'.
						This is useful when you need to specify something in one of the
						two fields, but not the other.
					<P>The '-' character is used to specify ranges For example
						&quot;10-12&quot; in the hour field means &quot;the hours 10, 11
						and 12&quot;.
					<P>The ',' character is used to specify additional values. For
						example &quot;MON,WED,FRI&quot; in the day-of-week field means
						&quot;the days Monday, Wednesday, and Friday&quot;.
					<P>The '/' character is used to specify increments. For example
						&quot;0/15&quot; in the seconds field means &quot;the seconds 0,
						15, 30, and 45&quot;. And &quot;5/15&quot; in the seconds field
						means &quot;the seconds 5, 20, 35, and 50&quot;. Specifying ''
						before the '/' is equivalent to specifying 0 is the value to start
						with. Essentially, for each field in the expression, there is a
						set of numbers that can be turned on or off. For seconds and
						minutes, the numbers range from 0 to 59. For hours 0 to 23, for
						days of the month 0 to 31, and for months 1 to 12. The
						&quot;/&quot; character simply helps you turn on every
						&quot;nth&quot; value in the given set. Thus &quot;7/6&quot; in
						the month field only turns on month &quot;7&quot;, it does NOT
						mean every 6th month, please note that subtlety.
					<P>
						The 'L' character is allowed for the day-of-month and day-of-week
						fields. This character is short-hand for &quot;last&quot;, but it
						has different meaning in each of the two fields. For example, the
						value &quot;L&quot; in the day-of-month field means &quot;the last
						day of the month&quot; - day 31 for January, day 28 for February
						on non-leap years. If used in the day-of-week field by itself, it
						simply means &quot;7&quot; or &quot;SAT&quot;. But if used in the
						day-of-week field after another value, it means &quot;the last xxx
						day of the month&quot; - for example &quot;6L&quot; means
						&quot;the last friday of the month&quot;. You can also specify an
						offset from the last day of the month, such as "L-3" which would
						mean the third-to-last day of the calendar month. <i>When
							using the 'L' option, it is important not to specify lists, or
							ranges of values, as you'll get confusing/unexpected results.</i>
					<P>The 'W' character is allowed for the day-of-month field.
						This character is used to specify the weekday (Monday-Friday)
						nearest the given day. As an example, if you were to specify
						&quot;15W&quot; as the value for the day-of-month field, the
						meaning is: &quot;the nearest weekday to the 15th of the
						month&quot;. So if the 15th is a Saturday, the trigger will fire
						on Friday the 14th. If the 15th is a Sunday, the trigger will fire
						on Monday the 16th. If the 15th is a Tuesday, then it will fire on
						Tuesday the 15th. However if you specify &quot;1W&quot; as the
						value for day-of-month, and the 1st is a Saturday, the trigger
						will fire on Monday the 3rd, as it will not 'jump' over the
						boundary of a month's days. The 'W' character can only be
						specified when the day-of-month is a single day, not a range or
						list of days.
					<P>The 'L' and 'W' characters can also be combined for the
						day-of-month expression to yield 'LW', which translates to
						&quot;last weekday of the month&quot;.
					<P>The '#' character is allowed for the day-of-week field. This
						character is used to specify &quot;the nth&quot; XXX day of the
						month. For example, the value of &quot;6#3&quot; in the
						day-of-week field means the third Friday of the month (day 6 =
						Friday and &quot;#3&quot; = the 3rd one in the month). Other
						examples: &quot;2#1&quot; = the first Monday of the month and
						&quot;4#5&quot; = the fifth Wednesday of the month. Note that if
						you specify &quot;#5&quot; and there is not 5 of the given
						day-of-week in the month, then no firing will occur that month. If
						the '#' character is used, there can only be one expression in the
						day-of-week field (&quot;3#1,6#3&quot; is not valid, since there
						are two expressions).
					<P>
					<P>The legal characters and the names of months and days of the
						week are not case sensitive.
					<p>
						<b>NOTES:</b>
					<ul>
						<li>Support for specifying both a day-of-week and a
							day-of-month value is not complete (you'll need to use the '?'
							character in one of these fields).</li>
						<li>Overflowing ranges is supported - that is, having a
							larger number on the left hand side than the right. You might do
							22-2 to catch 10 o'clock at night until 2 o'clock in the morning,
							or you might have NOV-FEB. It is very important to note that
							overuse of overflowing ranges creates ranges that don't make
							sense and no effort has been made to determine which
							interpretation CronExpression chooses. An example would be "0 0
							14-6 ? FRI-MON".</li>
					</ul>
					</p>

				</section>
				<section id="status">
					<div class="page-header">
						<h1>状态说明</h1>
					</div>
					<table  class="table table-striped table-bordered table-condensed">
						<tr>
							<th align="left">状态名</th>
							<th align="left">解释</th>
						</tr>
						<tr>
							<td align="left">RUNNING</td>
							<td align="left">这个实例正在运行</td>
						</tr>
						<tr>
							<td align="left">DEPENDENCY_PASS</td>
							<td align="left">这个实例等待被调度运行</td>
						</tr>
						<tr>
							<td align="left">DEPENDENCY_TIMEOUT</td>
							<td align="left">实例等待被调度超时，但它依然可被调度。只要当它依赖的作业完成，或者它的前一次实例完成，即可被调度执行</td>
						</tr>
						<tr>
							<td align="left">SUCCEEDED</td>
							<td align="left">这个实例已经成功执行</td>
						</tr>
						<tr>
							<td align="left">FAILED</td>
							<td align="left">这个实例因为返回值不为0而被系统认为执行失败</td>
						</tr>
						<tr>
							<td align="left">KILLED</td>
							<td align="left">这个实例被杀死</td>
						</tr>
						<tr>
							<td align="left">TIMEOUT</td>
							<td align="left">当运行的实例执行时间超过配置的最大执行时间时，将被认为超时，但这个实例依然继续运行</td>
						</tr>
					</table>
				</section>
				
				<section id="autokill">
					<h1>自动杀死Timeout实例</h1>
					<p>如果有实例执行超时了，并且已经有新的实例在等待执行，那么系统将自动将这个Timeout的实例杀死。</p>
					
					<p>
					Note: 对每个作业，系统的默认配置是开启这个功能的。如需要修改，请在配置时设置<span class="label label-info">自动kill timeout实例
					</span>为否。</p>
					
				</section>
			</div>

		</div>
	</div>

</body>
</html>