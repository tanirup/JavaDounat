# JavaDounat

## チームメンバーが最初にすること

### 1. プロジェクトをクローンする

最初に、GitHubのリポジトリ画面で「Code」からHTTPSのURLをコピーし、Eclipseまたはターミナルを使って自分のPCへクローンします。

ターミナルを使う場合：

```bash
git clone https://github.com/tanirup/JavaDounat.git
cd JavaDounat
```

Eclipseを使う場合は、「File」→「Import」→「Git」→「Projects from Git」→「Clone URI」を選び、コピーしたURLを入力します。

### 2. Gitを設定する

クローン後、このPCで初めてGitを使う場合だけ、名前とメールアドレスを設定します。これは、誰がコミットしたか分かるようにするための設定です。

```bash
git config --global user.name "自分の名前またはGitHubユーザー名"
git config --global user.email "GitHubに登録したメールアドレス"
```

すでに設定済みの場合は、もう一度実行する必要はありません。次のコマンドで確認できます。

```bash
git config --global --list
```

> **重要：** `git config`はクローン前でなくても問題ありませんが、最初のコミットを行う前には設定してください。GitHubへのログイン・認証とは別の設定です。

## 毎回の作業手順

### 1. 作業前に最新状態を取り込む

```bash
git switch main
git pull origin main
```

### 2. 自分の作業用ブランチを作る

ブランチとは、`main`を直接変更せずに作業するための「自分専用の作業場所」です。失敗しても`main`にはすぐ影響しないため、チーム開発ではブランチを使います。

最初に、担当する作業が分かる名前でブランチを作ります。

```bash
git switch -c feature-作業名
```

たとえば、ログイン画面を作る場合：

```bash
git switch -c feature-login
```

このコマンドは、`feature-login`というブランチを新しく作り、そのブランチへ移動するという意味です。現在いるブランチを確認するときは、次を実行します。

```bash
git branch
```

先頭に`*`が付いているものが、現在作業中のブランチです。

```text
  main
* feature-login
```

ブランチ名の例：

- ログイン画面：`feature-login`
- 投稿機能：`feature-post`
- デザイン修正：`fix-design`

> **作業の流れ：** mainを最新にする → 自分のブランチを作る → 編集する → pushする → Pull Requestを作る

### 3. コードを編集して変更を確認する

```bash
git status
git diff
```

### 4. 変更を記録する

```bash
git add .
git commit -m "変更内容を書く"
```

コミットメッセージには、「修正」だけではなく、何を変更したかを書いてください。

例：

```bash
git commit -m "ログイン画面を追加"
```

### 5. GitHubへプッシュする

最初のプッシュ：

```bash
git push -u origin feature-作業名
```

同じブランチで2回目以降：

```bash
git push
```
## EclipseからGitHubへプッシュする方法

ターミナルを使わず、Eclipseから直接コミット・プッシュすることもできます。

### 1. 現在のブランチを確認する

Eclipseの「パッケージ・エクスプローラー」でプロジェクト名を確認します。

例：

MainPage [JavaDounat feature-mainpage]

この場合、現在は `feature-mainpage` ブランチで作業しています。

mainブランチで直接作業せず、自分の作業用ブランチになっていることを確認してください。

---

### 2. 変更内容をコミットする

パッケージ・エクスプローラーで、プロジェクト名を右クリックします。

操作：

チーム
→ コミット...

Gitステージング画面が表示されたら、変更したファイルを「ステージされた変更」へ移動します。

コミットメッセージには、何を変更したのか分かる内容を書きます。

例：

MainPageを作成

ログイン画面を追加

注文処理を修正

その後、「コミット」を押します。

---

### 3. Pushする前にGitHub側の変更を取り込む

他のメンバーが同じブランチや関連する内容を更新している可能性があるため、Pushする前にPullを行います。

プロジェクト名を右クリックして、

チーム
→ プル...

を選択します。

正常に取り込めた場合、

結果：マージ済み

などと表示されます。

---

### 4. GitHubへPushする

Pullが完了したら、再びプロジェクト名を右クリックします。

次のどちらかを選択します。

チーム
→ ブランチのプッシュ 'feature-作業名'...

または

チーム
→ HEAD のプッシュ...

現在作業しているブランチがGitHubへアップロードされます。

---

### 5. GitHubへのログインを求められた場合

GitHubでは、HTTPSでPushする場合、通常のGitHubパスワードではなくPersonal Access Tokenを使用します。

入力内容：

User
→ GitHubのユーザー名

Password
→ Personal Access Token

例：

User:
tanirup

Password:
github_pat_xxxxxxxxxx

Personal Access TokenはGitHubの

Settings
→ Developer settings
→ Personal access tokens
→ Fine-grained tokens

から作成できます。

Pushだけを行う場合、基本的には次の権限で問題ありません。

Repository access
→ JavaDounatを選択

Repository permissions
→ Contents
→ Read and write

注意：
Personal Access Tokenはパスワードと同じ重要な情報です。
GitHub、README、Discord、LINE、スクリーンショットなどに絶対に公開しないでください。

---

### 6. non-fast-forwardエラーが出た場合

Pushした際に、

rejected - non-fast-forward

と表示される場合があります。

これは、GitHub側に自分のPCへ取り込んでいない変更が存在しているという意味です。

この場合は、強制Pushをせず、次の順番で操作します。

プロジェクト名を右クリック
↓
チーム
↓
プル...
↓
GitHub側の変更を取り込む
↓
必要であればマージ
↓
もう一度Push

正常にPullできると、

結果：マージ済み

などと表示されます。

その後、

チーム
→ ブランチのプッシュ 'feature-作業名'...

または

チーム
→ HEAD のプッシュ...

を実行します。

---

## Eclipseでの基本的な作業の流れ

作業用ブランチでコードを編集
↓
コミット
↓
Pull
↓
必要ならマージ
↓
Push
↓
GitHubでPull Requestを作成
↓
チームメンバー・管理者が確認
↓
mainへマージ

---

## ターミナルとEclipseの対応表

| やりたいこと | ターミナル | Eclipse |
|---|---|---|
| 変更確認 | git status | Gitステージング |
| コミット | git commit | チーム → コミット |
| GitHub側の変更取得 | git pull | チーム → プル |
| Push | git push | チーム → HEADのプッシュ |
| ブランチ確認 | git branch | プロジェクト名横のブランチ表示 |
| ブランチ作成 | git switch -c | チーム → 切り替え → 新規ブランチ |

---

## Eclipseを使う場合の注意

- ファイル単体ではなく、プロジェクト名を右クリックする。
- mainブランチへ直接Pushしない。
- Push前にPullして最新状態を確認する。
- `non-fast-forward` が出た場合は、強制PushせずPullする。
- コンフリクトが出た場合は勝手に削除せず、チームメンバーへ相談する。
- `.env`、APIキー、パスワード、Personal Access Tokenはコミットしない。

### 6. Pull Requestを作る

Pull Requestは、「自分のブランチで行った変更を`main`へ入れてよいか確認してもらう機能」です。

1. GitHubで対象のリポジトリを開きます。
2. 「Compare & pull request」を押します。
3. 何を変更したか入力してPull Requestを作ります。
4. チームメンバーに内容を確認してもらいます。
5. 問題がなければ`main`へマージします。

#### `feature-login`で作業した場合の例

まず、ログイン画面の変更をコミットし、`feature-login`ブランチをGitHubへプッシュします。

```bash
git switch feature-login
git add .
git commit -m "ログイン画面を追加"
git push -u origin feature-login
```

プッシュ後、GitHubで`JavaDounat`リポジトリを開き、「Compare & pull request」を押します。次の組み合わせになっていることを確認してください。

```text
base: main ← 変更を入れる完成版
compare: feature-login ← 自分が作業したブランチ
```

Pull Requestには、たとえば次のように入力します。

```text
タイトル：ログイン画面を追加

変更内容：
- ログイン画面を追加しました
- メールアドレスとパスワードの入力欄を作りました

確認してほしいこと：
- 正しく画面が表示されるか
- スマートフォンでレイアウトが崩れないか
```

入力後に「Create pull request」を押し、管理者へ確認をお願いします。自分で`main`へマージせず、確認が終わるまで待ちます。

マージ後に次の作業を始めるときは、再び`main`へ戻して最新状態を取り込み、新しいブランチを作ります。

```bash
git switch main
git pull origin main
git switch -c feature-次の作業名
```

## チーム内のルール

- 作業を始める前に、必ず`main`で`git pull origin main`を実行する。
- `main`へ直接プッシュせず、作業用ブランチを使う。
- コミットメッセージには変更内容を分かりやすく書く。
- `.env`、パスワード、APIキー、秘密鍵は絶対にプッシュしない。
- 担当するファイルや機能を事前に共有する。
- 分からないエラーやコンフリクトが出たら、無理に操作せずチームに相談する。

## よく使うコマンド

```bash
git status                         # 変更状況を確認
git switch main                    # mainへ移動
git pull origin main               # 最新状態を取り込む
git switch -c feature-作業名       # 作業用ブランチを作成
git add .                          # 変更を追加
git commit -m "変更内容"           # 変更を記録
git push -u origin feature-作業名  # 初回のプッシュ
git push                           # 2回目以降のプッシュ
```

## みんなの変更を完成版にまとめる方法

それぞれが作ったコードは、自分の作業用ブランチからGitHubへプッシュします。

たとえば、`feature-login`ブランチで作業した場合：

```bash
git add .
git commit -m "ログイン画面を追加"
git push -u origin feature-login
```

ただし、プッシュしただけでは`main`に反映されません。プッシュ後にGitHubでPull Requestを作り、内容を確認してから`main`へマージします。

1. 各自が自分のブランチで作業する。
2. 自分のブランチをGitHubへプッシュする。
3. GitHubでPull Requestを作る。
4. チームメンバーが変更内容を確認する。
5. 問題がなければ`main`へマージする。

```text
各自のブランチ
↓ push
GitHub
↓ Pull Request
内容を確認
↓ merge
mainに全員のコードがまとまる
```

全員のPull Requestを`main`へマージすると、完成した機能が1つにまとまります。マージ後は、各自のPCでも完成版を取り込みます。

```bash
git switch main
git pull origin main
```

> **注意：** 同じファイルの同じ部分を複数人が編集すると、コンフリクトが発生することがあります。事前に担当するファイルや機能を分けておくと安全です。

## Pull Requestの作り方

Pull Requestは、基本的に各メンバーが自分のブランチをプッシュした後、自分で作成します。

1. 自分の作業用ブランチをGitHubへプッシュする。
2. GitHubで`JavaDounat`リポジトリを開く。
3. 表示された「Compare & pull request」を押す。
4. `base: main`、`compare: 自分のブランチ名`になっているか確認する。
5. タイトルに何を変更したか書く。
6. 説明欄に作業内容や確認してほしいことを書く。
7. 「Create pull request」を押す。
8. 管理者へ確認をお願いする。

```text
自分のブランチ（compare）
↓ Pull Requestを作成
main（base）
```

タイトルの例：

```text
ログイン画面を追加
```

説明欄の例：

```text
## 変更内容
- ログイン画面を追加しました
- 入力フォームのデザインを調整しました

## 確認してほしいこと
- スマートフォンでもレイアウトが崩れないか
```

> **ルール：** Pull Requestは各メンバーが自分で作成します。管理者が確認する前に、自分で`main`へマージしないでください。
