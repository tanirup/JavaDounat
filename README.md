# JavaDounat

JavaDounatのチーム開発用Git / GitHubルールです。

---

# まず最初にやること

## 1. GitHubの招待を承認する

管理者からJavaDounatへの招待が届いたら承認してください。

---

## 2. JavaDounatをクローンする

```bash
git clone https://github.com/tanirup/JavaDounat.git
cd JavaDounat
```

Eclipseを使う場合は、

```text
File
→ Import
→ Git
→ Projects from Git
→ Clone URI
```

からクローンしてください。

---

## 3. Gitの名前とメールを設定する

初回だけ設定します。

```bash
git config --global user.name "自分のGitHubユーザー名"
git config --global user.email "自分のメールアドレス"
```

確認：

```bash
git config --global --list
```

---

# 作業を始めるとき

## 1. mainを最新にする

```bash
git switch main
git pull origin main
```

---

## 2. 自分の作業ブランチを作る

```bash
git switch -c feature-作業名
```

例：

```bash
git switch -c feature-login
```

```bash
git switch -c feature-mainpage
```

現在のブランチ確認：

```bash
git branch
```

`*`が付いているブランチが現在のブランチです。

---

# コードを書いたあと

## 1. 変更を確認

```bash
git status
```

---

## 2. 変更を追加

全部追加する場合：

```bash
git add .
```

1ファイルだけ追加する場合：

```bash
git add src/Main.java
```

---

## 3. コミット

```bash
git commit -m "変更内容"
```

例：

```bash
git commit -m "ログイン画面を追加"
```

---

## 4. GitHubへPush

初回：

```bash
git push -u origin feature-作業名
```

例：

```bash
git push -u origin feature-login
```

2回目以降：

```bash
git push
```

---

# Pushするときにログインを求められた場合

GitHubの通常のパスワードは使いません。

```text
Username
→ 自分のGitHubユーザー名

Password
→ Personal Access Token
```

Tokenは各メンバーが自分用に作成してください。

```text
GitHub
→ Settings
→ Developer settings
→ Personal access tokens
→ Tokens (classic)
```

権限は基本、

```text
repo
```

にチェックでOKです。

Tokenは他の人と共有しないでください。

---

# Pull Requestを作る

PushしたらGitHubでJavaDounatを開きます。

```text
Compare & pull request
```

を押します。

次の状態になっていることを確認してください。

```text
base: main
compare: 自分のブランチ
```

例：

```text
base: main
compare: feature-login
```

タイトル例：

```text
ログイン画面を追加
```

説明例：

```text
## 変更内容
- ログイン画面を追加しました
- 入力欄を追加しました

## 確認してほしいこと
- 正しく表示されるか
- エラーが出ないか
```

最後に、

```text
Create pull request
```

を押してください。

管理者が確認するまで、自分でmainへマージしないでください。

---

# マージされたあとの作業

mainへ戻します。

```bash
git switch main
```

最新状態を取り込みます。

```bash
git pull origin main
```

次の作業をするときは、新しいブランチを作ります。

```bash
git switch -c feature-次の作業名
```

---

# Eclipseを使う場合

## コミット

```text
プロジェクトを右クリック
→ チーム
→ コミット
```

変更したファイルをステージして、コミットメッセージを書きます。

---

## Push

```text
プロジェクトを右クリック
→ チーム
→ HEAD のプッシュ
```

または、

```text
ブランチのプッシュ
```

を選択します。

---

# Pushできない場合

## non-fast-forward

```text
rejected - non-fast-forward
```

と出た場合は、GitHub側に自分が持っていない変更があります。

Eclipse：

```text
プロジェクト右クリック
→ チーム
→ プル
→ もう一度Push
```

ターミナル：

```bash
git pull
git push
```

※ 強制Pushはしないでください。

---

# チームルール

- mainへ直接Pushしない
- 必ず自分のfeatureブランチを使う
- 作業前にmainを最新にする
- コミットメッセージは分かりやすく書く
- Tokenやパスワードを共有しない
- `.env`やAPIキーをPushしない
- 強制Pushはしない
- エラーが出たら無理に操作しない

---

# 一番大事な流れ

```text
mainを最新にする
↓
自分のブランチを作る
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
```

---

# よく使うコマンド

```bash
git status
git branch

git switch main
git pull origin main

git switch -c feature-作業名

git add .
git commit -m "変更内容"

git push -u origin feature-作業名
git push
```
