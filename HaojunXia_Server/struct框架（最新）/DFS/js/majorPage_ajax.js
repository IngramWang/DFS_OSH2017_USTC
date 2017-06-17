$(document).ready(function(){
	
	var curr_path_array = new Array();
	curr_path_array[0] = "/";
	curr_path_html = "<li>ROOT</li>";
	
	//面包屑式访问路径显示  初始化
	$("#curr_path").html(curr_path_html);
	
	//
	$("#button_download").click(
	function()
		{
			var string='';
			var item=$("#file_list_first");

			item = item.next();
			//while(item.length!=0)
				{
					//如果ｉｔｅｍ不为空，则进行处理
					var children=item.children();
					string = string + "  "+children[1].innerText+ "  "+ children[1].children[0].children[0].checked+ "  " + children[2].innerHTML +"  "+children[3].innerHTML ;
					//
					item = item.next();
				}
			alert(string);
		}
	);
	/*
		<tr id="file_list_first">
		<td> </td>
 		<td> <label><input type="checkbox">&emsp;&emsp;</label><span class="glyphicon glyphicon-folder-open"></span>&emsp;../</td>
 		<td></td>
 		<td></td>
		</tr>

*/
	//点击文件目录进入其子目录　　刷新文件目录列表
	$("#file_list_body").on("click","tr.file_list_go",
			function()
			{
				//如果是文件而不是文件夹，点击不刷新目录，提示信息
				if(this.children[1].children[1].className=="glyphicon glyphicon-file")
				{
					$("#statusFeedback").text("您所点击的是文件而不是文件夹，无法进入该目录！");
					return;
				}
				//更新路径显示
				curr_path_array = curr_path_array.concat( $.trim(this.children[1].innerText) );			//此处用$.trim去除空格
				curr_path_html = "<li>ROOT</li>";
				for(var i=1;i<curr_path_array.length;i++)
				curr_path_html = curr_path_html + "<li>" + curr_path_array[i] + "</li>";
				$("#curr_path").html(curr_path_html);		
				//ajax
				var QueryPath="/";
				for(var i=1;i<curr_path_array.length;i++)
				{
					QueryPath = QueryPath + curr_path_array[i] + "/" ;
				}
				var	form=new FormData();
				form.append("QueryPath",QueryPath);
				
				//alert(queryPath);
				$.ajax({
						url:"GetFileList.action",
						type:"POST",
						data:form,
						dataType:"text",
						processData:false,
						contentType:false,
						success:function(databack){
							var obj = $.parseJSON(databack);
							var new_file_list = obj.html;
							//alert(new_file_list);
							$("#file_list_body").html(new_file_list);
						}
				});
				$("#statusFeedback").text("成功进入该目录！");
			}
	);
	
	//点击的是返回上一层的文件项
	$("#file_list_body").on("click","tr.file_list_back",
			function()
			{
				//如果是顶层目录，点击上级目录无操作，提示信息
				if(curr_path_array.length==1)
				{
					$("#statusFeedback").text("已经是根目录了，无法返回上一层！");
					return; 
				}
				//更新路径显示
				curr_path_array.pop();
				curr_path_html = "<li>ROOT</li>";
				for(var i=1;i<curr_path_array.length;i++)
				curr_path_html = curr_path_html + "<li>" + curr_path_array[i] + "</li>";
				$("#curr_path").html(curr_path_html);	
				
				//ajax
				var QueryPath="/";
				for(var i=1;i<curr_path_array.length;i++)
				{
					QueryPath = QueryPath + curr_path_array[i] + "/" ;
				}
				var	form=new FormData();
				form.append("QueryPath",QueryPath);
				
				//alert(queryPath);
				$.ajax({
						url:"GetFileList.action",
						type:"POST",
						data:form,
						dataType:"text",
						processData:false,
						contentType:false,
						success:function(databack){
							var obj = $.parseJSON(databack);
							var new_file_list = obj.html;
							//alert(new_file_list);
							$("#file_list_body").html(new_file_list);
						}
				});
				$("#statusFeedback").text("成功返回上层目录！");
			}
	);


	
//总的结束符	
});

/*
   			<tr id="file_list_first">
      			<td> </td>
         		<td> <label><input type="checkbox">&emsp;&emsp;</label><span class="glyphicon glyphicon-folder-open"></span>&emsp;../</td>
         		<td></td>
         		<td></td>
      		</tr>
 
 */