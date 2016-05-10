package com.zs.product;

import java.util.List;

import com.zs.product.bean.Account;
import com.zs.product.dao.AccountDao;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.media.Image;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	// 需要适配的数据集合
	private List<Account> list;
	// 数据库增删改差操作类
	private AccountDao dao;
	// 输入名称的EditText
	private EditText nameET;
	// 输入金额的EditText
	private EditText balanceET;
	// 适配器
	private MyAdapter adapter;
	// ListView
	private ListView accountLV;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// 初始化控件
		initView();
		dao = new AccountDao(this);
		// 从数据库查询出所有数据
		list = dao.queryAll();
		adapter = new MyAdapter();
		// 为ListView添加适配器（自动把数据生成条目）
		accountLV.setAdapter(adapter);
	}

	private void initView() {
		accountLV = (ListView) findViewById(R.id.accountLV);
		nameET = (EditText) findViewById(R.id.nameET);
		balanceET = (EditText) findViewById(R.id.balanceET);
		// 添加监听器，监听条目点击事件
		accountLV.setOnItemClickListener(new MyOnItemClickListener());

	}

	public void add(View v) {
		String name = nameET.getText().toString().trim();
		String balance = balanceET.getText().toString().trim();
		// 三目运算balance.equals("")则等于0
		// 如果balance不是空字符串则进行类型转换
		Account a = new Account(name, balance.equals("") ? 0
				: Integer.parseInt(balance));
		dao.insert(a);// 插入数据库
		list.add(a);// 插入集合
		adapter.notifyDataSetChanged();// 刷新界面
		// 选中最后一个
		accountLV.setSelection(accountLV.getCount() - 1);
		nameET.setText("");
		balanceET.setText("");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// 自定义一个适配器（把数据装载到ListView的工具）
	private class MyAdapter extends BaseAdapter {
		// 获取条目总数
		public int getCount() {
			return list.size();
		}

		// 根据位置获取对象
		public Object getItem(int position) {
			return list.get(position);
		}

		// 根据位置获取id
		public long getItemId(int position) {
			return position;
		}

		// 获取一个条目视图
		public View getView(int position, View convertView, ViewGroup parent) {
			System.out.println("11111111111111111");
			// 重用convertView
			View item = convertView != null ? convertView : View.inflate(
					getApplicationContext(), R.layout.item, null);
			// 获取该视图中的TextView
			TextView idTV = (TextView) item.findViewById(R.id.idTV);
			TextView nameTV = (TextView) item.findViewById(R.id.nameTV);
			TextView balanceTV = (TextView) item.findViewById(R.id.balanceTV);
			// 根据当前位置获取Account对象
			final Account a = list.get(position);
			// 把Account对象中的数据放到TextView中
			idTV.setText(a.getId() + "");
			nameTV.setText(a.getName());
			balanceTV.setText(a.getBalance() + "");
			ImageView upIV = (ImageView) item.findViewById(R.id.upIV);
			ImageView downIV = (ImageView) item.findViewById(R.id.downIV);
			ImageView deleteIV = (ImageView) item.findViewById(R.id.deleteIV);

			// 向上箭头的点击事件触发的方法
			upIV.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					a.setBalance(a.getBalance() + 1);// 修改值
					adapter.notifyDataSetChanged();// 刷新界面
					dao.update(a);// 更新数据库
				}
			});
			// 向下箭头的点击事件触发的方法
			downIV.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					a.setBalance(a.getBalance() - 1);
					adapter.notifyDataSetChanged();
					dao.update(a);

				}
			});

			// 删除图片的点击事件触发方法
			deleteIV.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					// 删除数据之前首先弹出一个对话框
					android.content.DialogInterface.OnClickListener listener = new android.content.DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							list.remove(a);// 从集合中删除
							dao.delete(a.getId());// 从数据库中删除
							adapter.notifyDataSetChanged();// 刷新界面
						}
					};
					// 创建对话框
					Builder bulider = new Builder(MainActivity.this);
					bulider.setTitle("确定要删除吗？");// 设置标题
					// 设置确定按钮的文本以及监听器
					bulider.setPositiveButton("确定", listener);
					bulider.setNegativeButton("取消", listener);
					bulider.show();
				}
			});
			return item;

		}
	}

	private class MyOnItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Account a = (Account) parent.getItemAtPosition(position);
			Toast.makeText(getApplicationContext(), a.toString(), Toast.LENGTH_SHORT).show();

		}
	}
}
