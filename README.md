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

# 最初にやること

## 1. GitHub の招待を承認する

管理者から届いた JavaDounat リポジトリへの招待を承認してください。

## 2. リポジトリをクローンする

```bash
git clone https://github.com/tanirup/JavaDounat.git
cd JavaDounat
code .
```

## 3. Git の名前とメールを設定する

初回だけ設定します。

```bash
git config --global user.name "自分のGitHubユーザー名"
git config --global user.email "自分のメールアドレス"
```

## 4. Personal Access Token を作る

GitHub で、

```text
Settings
→ Developer settings
→ Personal access tokens
→ Tokens (classic)
```

から作成します。

権限は、

```text
repo
```

にチェックしてください。

Token は他の人と共有しないでください。

---

# 作業を始めるとき

まず main を最新にします。

```bash
git switch main
git pull origin main
```

次に、自分の作業ブランチを作ります。

```bash
git switch -c feature-作業名
```

例：

```bash
git switch -c feature-login
```

現在のブランチ確認：

```bash
git branch
```

---

# コードを書いたあと

変更を確認します。

```bash
git status
```

変更を追加します。

```bash
git add .
```

コミットします。

```bash
git commit -m "変更内容"
```

例：

```bash
git commit -m "ログイン画面を追加"
```

---

## Git のプッシュ先を確認する

### 現在の接続先を確認

```bash
git remote -v
```

### プッシュ先のリポジトリを変更

```bash
git remote set-url origin URL
```

※ `URL` には変更先の GitHub リポジトリ URL を入力してください。

# GitHub へ Push する

初回 Push：

```bash
git push -u origin feature-作業名
```

例：

```bash
git push -u origin feature-login
```

2 回目以降：

```bash
git push
```

---

# GitHub のログインを求められたとき

```text
Username
→ 自分のGitHubユーザー名

Password
→ Personal Access Token
```

GitHub の通常のパスワードは使いません。

---

# Pull Request を作る

Push したら GitHub で JavaDounat を開きます。

```text
Compare & pull request
```

を押します。

次の状態になっているか確認します。

```text
base: main
compare: 自分のブランチ
```

例：

```text
base: main
compare: feature-login
```

その後、

```text
Create pull request
```

を押します。

管理者が確認するまで、自分で main へマージしないでください。

---

# main へマージされたあと

main へ戻ります。

```bash
git switch main
```

最新状態を取り込みます。

```bash
git pull origin main
```

次の作業を始める場合は、新しいブランチを作ります。

```bash
git switch -c feature-次の作業名
```

---

# Push できないとき

現在のブランチを確認します。

```bash
git branch
```

例えば `feature-login` ：

```bash
git pull origin feature-login
git push
```

分からない場合は強制 Push しないでください。

```bash
git push --force
```

は使用しないでください。

---

# 作業終了時にやること

その日の作業が終わったら、自分の作業ブランチへ Push します。

## 1. 現在のブランチを確認

```bash
git branch
```

`*`が付いているものが現在のブランチです。

例：

```text
  main
* feature-login
```

---

## 2. 変更内容を確認

```bash
git status
```

---

## 3. 変更を追加

```bash
git add .
```

---

## 4. コミット

```bash
git commit -m "作業内容"
```

例：

```bash
git commit -m "ログイン画面を作成"
```

---

## 5. 自分のブランチへ Push

## Git のプッシュ先を確認する

### 現在の接続先を確認

```bash
git remote -v
```

### プッシュ先のリポジトリを変更

```bash
git remote set-url origin URL
```

※ `URL` には変更先の GitHub リポジトリ URL を入力してください。

初回 Push：

```bash
git push -u origin 自分のブランチ名
```

例：

```bash
git push -u origin feature-login
```

2 回目以降：

```bash
git push
```

---

## 6. 作業が完成した場合

GitHub で Pull Request を作成します。

```text
自分のブランチ
↓
Push
↓
Pull Request
↓
管理者が確認
↓
mainへマージ
```

---

# ブランチの削除・他の人のブランチを使う方法

## ブランチを削除する

### 自分の PC から削除

まず `main` に移動します。

```bash
git switch main
```

削除：

```bash
git branch -d ブランチ名
```

例：

```bash
git branch -d feature-login
```

### GitHub 上から削除

```bash
git push origin --delete ブランチ名
```

例：

```bash
git push origin --delete feature-login
```

GitHub の `Branches` 画面から `Delete branch` を押して削除することもできます。

---

## 他の人のブランチを使いたい場合

まだリポジトリを clone していない場合：

```bash
git clone -b ブランチ名 リポジトリURL
```

例：

```bash
git clone -b feature-login https://github.com/tanirup/JavaDounat.git
```

すでに JavaDounat を clone 済みの場合：

```bash
git fetch origin
git switch ブランチ名
```

例：

```bash
git fetch origin
git switch feature-login
```

---

## 注意

- 他の人のブランチは勝手に削除しない
- 削除前に必要なコードが残っていないか確認する
- 分からない場合は `git status` と `git branch` を確認する

ーーーーーーーーー

- `main`へ直接 Push しない
- 必ず自分の作業ブランチへ Push する
- 作業途中でも自分のブランチへの Push は OK
- 完成したら Pull Request を作る
- `git push --force`は使わない

# チームルール

- main へ直接 Push しない
- 必ず feature ブランチを使う
- 作業前に main を Pull する
- コミットメッセージは変更内容が分かるようにする
- Token やパスワードを共有しない
- `.env`や API キーを Push しない
- 強制 Push はしない
- 分からないエラーが出たら勝手に操作しない

---

# 基本の流れ

```text
GitHubの招待を承認
↓
clone
↓
mainを最新にする
↓
featureブランチを作る
↓
コードを書く
↓
git add
↓
git commit
↓
git push
↓
Pull Request
↓
レビュー
↓
mainへマージ
↓
mainをPull
↓
次の作業へ
```

---

# よく使うコマンド

```bash
git status
git branch

git switch main
git pull origin main

git switch -c feature-作業名
git switch feature-作業名

git add .
git commit -m "変更内容"

git push -u origin feature-作業名
git push
```
