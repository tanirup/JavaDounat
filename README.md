# JavaDounat

JavaDounat のチーム開発用 Git / GitHub ルールです。

---

#　追記その１！！

## MySQL セットアップ

Docker を使用して MySQL 環境を構築します。

### 1. ブランチを取得

```bash
git clone -b feature-Dounat_db リポジトリURL
cd JavaDounat
```

### 2. 環境変数を設定

```bash
copy .env.example .env
```

`.env` に各自のパスワードを設定してください。
<br><br>
ターミナルでやること

```bash
nano .gitignore
```

```bash
.env
.env.*
!.env.example
```

この３つをコピペで入れること

※ `.env` は GitHub にプッシュしないでください。

```bash
git check-ignore -v .env
```
これで除外設定が表示されたらOK！

### 3. Docker を起動

Docker Desktop を起動後、以下を実行します。

```bash
docker compose up -d
```

### 4. データベースを確認

```bash
docker compose exec mysql mysql -u dounat_user -p dounat_db
```

```sql
SHOW TABLES;
```

6 つのテーブルが表示されれば完了です。

### 5. Docker の操作

| 操作     | コマンド               |
| -------- | ---------------------- |
| 起動     | `docker compose up -d` |
| 停止     | `docker compose stop`  |
| 再起動   | `docker compose start` |
| 状態確認 | `docker compose ps`    |

**注意：** 初回起動時に SQL が自動実行されます。既存の DB には自動で再実行されません。


