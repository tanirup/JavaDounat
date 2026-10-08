# JavaDounat

JavaDounatのチーム開発用Git / GitHubルールです。

---

# 最初にやること

## 1. GitHubの招待を承認する

管理者から届いたJavaDounatリポジトリへの招待を承認してください。

## 2. リポジトリをクローンする

```bash
git clone https://github.com/tanirup/JavaDounat.git
cd JavaDounat
code .
```

## 3. Gitの名前とメールを設定する

初回だけ設定します。

```bash
git config --global user.name "自分のGitHubユーザー名"
git config --global user.email "自分のメールアドレス"
```

## 4. Personal Access Tokenを作る

GitHubで、

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

Tokenは他の人と共有しないでください。

---

# 作業を始めるとき

まずmainを最新にします。

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

# GitHubへPushする

初回Push：

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

# GitHubのログインを求められたとき

```text
Username
→ 自分のGitHubユーザー名

Password
→ Personal Access Token
```

GitHubの通常のパスワードは使いません。

---

# Pull Requestを作る

PushしたらGitHubでJavaDounatを開きます。

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

管理者が確認するまで、自分でmainへマージしないでください。

---

# mainへマージされたあと

mainへ戻ります。

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

# Pushできないとき

現在のブランチを確認します。

```bash
git branch
```

例えば `feature-login` ：

```bash
git pull origin feature-login
git push
```

分からない場合は強制Pushしないでください。

```bash
git push --force
```

は使用しないでください。

---

# 作業終了時にやること

その日の作業が終わったら、自分の作業ブランチへPushします。

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

## 5. 自分のブランチへPush

初回Push：

```bash
git push -u origin 自分のブランチ名
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

## 6. 作業が完成した場合

GitHubでPull Requestを作成します。

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

## 注意

- `main`へ直接Pushしない
- 必ず自分の作業ブランチへPushする
- 作業途中でも自分のブランチへのPushはOK
- 完成したらPull Requestを作る
- `git push --force`は使わない

# チームルール

- mainへ直接Pushしない
- 必ずfeatureブランチを使う
- 作業前にmainをPullする
- コミットメッセージは変更内容が分かるようにする
- Tokenやパスワードを共有しない
- `.env`やAPIキーをPushしない
- 強制Pushはしない
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
