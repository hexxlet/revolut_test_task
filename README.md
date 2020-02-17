<h1 class="code-line" data-line-start=0 data-line-end=1 ><a id="Money_Transfer_App_0"></a>Money Transfer App</h1>
<p class="has-line-data" data-line-start="2" data-line-end="4">Task for Revolut.<br>
RESTful API for money transfers between accounts.</p>
<h2 class="code-line" data-line-start=5 data-line-end=6 ><a id="Technology_stack_5"></a>Technology stack</h2>
<ul>
<li class="has-line-data" data-line-start="6" data-line-end="7">Kotlin</li>
<li class="has-line-data" data-line-start="7" data-line-end="8">Sparkjava with embedded Jetty</li>
<li class="has-line-data" data-line-start="8" data-line-end="9">H2 database</li>
<li class="has-line-data" data-line-start="9" data-line-end="10">Mybatis</li>
<li class="has-line-data" data-line-start="10" data-line-end="11">Junit</li>
</ul>
<h2 class="code-line" data-line-start=13 data-line-end=14 ><a id="Run_the_app_13"></a>Run the app</h2>
<p class="has-line-data" data-line-start="14" data-line-end="16">To run the app you need Maven and Java 11 installed<br>
Clone project sources and then open terminal in project root</p>
<pre><code class="has-line-data" data-line-start="17" data-line-end="21" class="language-sh">$ mvn clean install
$ <span class="hljs-built_in">cd</span> target/
$ java -jar revolut_<span class="hljs-built_in">test</span>_task_kt-<span class="hljs-number">1.0</span>-SNAPSHOT-jar-with-dependencies.jar
</code></pre>
<p class="has-line-data" data-line-start="21" data-line-end="22">Application should start on port 8080</p>
<h1 class="code-line" data-line-start=23 data-line-end=24 ><a id="API_Description_23"></a>API Description</h1>
<p class="has-line-data" data-line-start="24" data-line-end="25">Application operates with 2 entities:</p>
<ul>
<li class="has-line-data" data-line-start="25" data-line-end="26">Account</li>
<li class="has-line-data" data-line-start="26" data-line-end="28">Operation</li>
</ul>
<table class="table table-striped table-bordered">
<thead>
<tr>
<th>Action</th>
<th>Path</th>
<th>HTTP Method</th>
</tr>
</thead>
<tbody>
<tr>
<td>create account</td>
<td>/accounts/new</td>
<td>POST</td>
</tr>
<tr>
<td>get account data</td>
<td>accounts/{id}</td>
<td>GET</td>
</tr>
<tr>
<td>transfer money</td>
<td>/transfers</td>
<td>POST</td>
</tr>
<tr>
<td>get operation history</td>
<td>accounts/history/{id}</td>
<td>GET</td>
</tr>
</tbody>
</table>
<h3 class="code-line" data-line-start=35 data-line-end=36 ><a id="Create_account_35"></a>Create account</h3>
<p class="has-line-data" data-line-start="36" data-line-end="37">available <a href="http://localhost:8080/accounts/new">http://localhost:8080/accounts/new</a> after start up</p>
<pre><code class="has-line-data" data-line-start="38" data-line-end="43">POST /accounts/new
{
    &quot;balance&quot;:&quot;0.11&quot;
}
</code></pre>
<p class="has-line-data" data-line-start="43" data-line-end="44">Example of successful response:</p>
<pre><code class="has-line-data" data-line-start="45" data-line-end="50">{
    &quot;accountId&quot;: &quot;5&quot;,
    &quot;status&quot;: &quot;OK&quot;
}
</code></pre>
<p class="has-line-data" data-line-start="50" data-line-end="51">Example of unsuccessful response:</p>
<pre><code class="has-line-data" data-line-start="52" data-line-end="57">{
    &quot;status&quot;: &quot;BAD&quot;,
    &quot;message&quot;: &quot;Account balance can not be less than 0&quot;
}
</code></pre>
<h3 class="code-line" data-line-start=57 data-line-end=58 ><a id="Get_account_data_57"></a>Get account data</h3>
<pre><code class="has-line-data" data-line-start="59" data-line-end="61">GET /accounts/{id}
</code></pre>
<p class="has-line-data" data-line-start="61" data-line-end="62">Example of successful response:</p>
<pre><code class="has-line-data" data-line-start="63" data-line-end="68">{
    &quot;id&quot;: &quot;4&quot;,
    &quot;balance&quot;: &quot;1993.50&quot;
}
</code></pre>
<p class="has-line-data" data-line-start="68" data-line-end="69">Example of unsuccessful response:</p>
<pre><code class="has-line-data" data-line-start="70" data-line-end="75">{
    &quot;status&quot;: &quot;BAD&quot;,
    &quot;message&quot;: &quot;No account found with id=5&quot;
}
</code></pre>
<h3 class="code-line" data-line-start=75 data-line-end=76 ><a id="Transfer_money_75"></a>Transfer money</h3>
<h5 class="code-line" data-line-start=76 data-line-end=77 ><a id="Important_note_76"></a>Important note!</h5>
<p class="has-line-data" data-line-start="77" data-line-end="79">Concurrent transactions on same accounts are handled.<br>
Optimistic lock used. Check implementation in OperationService class and AccountRepository interface.</p>
<pre><code class="has-line-data" data-line-start="80" data-line-end="87">POST /transfers
{
    &quot;from&quot;:&quot;1&quot;,
    &quot;to&quot;:&quot;5&quot;,
    &quot;amount&quot;:&quot;100&quot;
}
</code></pre>
<p class="has-line-data" data-line-start="87" data-line-end="88">Example of successful response:</p>
<pre><code class="has-line-data" data-line-start="89" data-line-end="93">{
    &quot;status&quot;: &quot;OK&quot;
}
</code></pre>
<p class="has-line-data" data-line-start="93" data-line-end="94">Example of unsuccessful response:</p>
<pre><code class="has-line-data" data-line-start="95" data-line-end="100">{
    &quot;status&quot;: &quot;BAD&quot;,
    &quot;message&quot;: &quot;Insufficient funds on account 1&quot;
}
</code></pre>
<h3 class="code-line" data-line-start=100 data-line-end=101 ><a id="Get_operation_history_100"></a>Get operation history</h3>
<pre><code class="has-line-data" data-line-start="102" data-line-end="104">GET /accounts/history/{id}
</code></pre>
<p class="has-line-data" data-line-start="104" data-line-end="105">Example of successful response:</p>
<pre><code class="has-line-data" data-line-start="106" data-line-end="125">[
    {
        &quot;operationType&quot;: &quot;INCOME&quot;,
        &quot;id&quot;: &quot;1&quot;,
        &quot;from&quot;: &quot;1&quot;,
        &quot;to&quot;: &quot;5&quot;,
        &quot;amount&quot;: &quot;10.00&quot;,
        &quot;time&quot;: &quot;2020-02-17 06:59:59&quot;
    },
    {
        &quot;operationType&quot;: &quot;OUTCOME&quot;,
        &quot;id&quot;: &quot;2&quot;,
        &quot;from&quot;: &quot;5&quot;,
        &quot;to&quot;: &quot;3&quot;,
        &quot;amount&quot;: &quot;10.00&quot;,
        &quot;time&quot;: &quot;2020-02-17 07:00:21&quot;
    }
]
</code></pre>
<h3 class="code-line" data-line-start=126 data-line-end=127 ><a id="Exceptions_126"></a>Exceptions</h3>
<p class="has-line-data" data-line-start="127" data-line-end="128">If an error happens response with <strong>500</strong> code returned</p>
<pre><code class="has-line-data" data-line-start="129" data-line-end="133">{
    &quot;status&quot;: &quot;BAD&quot;
}
</code></pre>